#!/bin/bash
set -e

echo "Starting new version deployment"

# 권한 체크
if [ "$(id -u)" != "0" ] && ! docker info > /dev/null 2>&1; then
    echo "이 스크립트는 루트 권한이나 docker 그룹 권한이 필요합니다."
    exit 1
fi

cd /home/ubuntu/develop/backend

NGINX_CONF="/home/ubuntu/develop/backend/data/nginx/nginx.conf"
HEALTH_CHECK_URL="http://localhost"
MAX_RETRIES=10
RETRY_INTERVAL=10

# 배포 로그 파일 설정
LOG_FILE="/home/ubuntu/develop/backend/deployment_$(date +%Y%m%d_%H%M%S).log"
exec > >(tee -a "$LOG_FILE") 2>&1

echo "$(date): 배포 시작"

# 현재 실행 중인 컨테이너 확인 및 새 컨테이너 설정
CURRENT_CONTAINER=$(docker ps --filter "name=campride-api-server" --format "{{.Names}}")

if [ -z "$CURRENT_CONTAINER" ]; then
    echo "경고: 실행 중인 컨테이너를 찾을 수 없습니다. 기본값으로 blue 컨테이너를 시작합니다."
    NEW_CONTAINER="campride-api-server-blue"
    NEW_SERVICE="app-blue"
    NEW_PORT=8080
    CURRENT_SERVICE=""
    CURRENT_PORT=""
elif [ "$CURRENT_CONTAINER" == "campride-api-server-blue" ]; then
    NEW_CONTAINER="campride-api-server-green"
    NEW_SERVICE="app-green"
    NEW_PORT=8081
    CURRENT_SERVICE="app-blue"
    CURRENT_PORT=8080
    echo "Current container: $CURRENT_CONTAINER (Port: $CURRENT_PORT)"
    echo "New container: $NEW_CONTAINER (Port: $NEW_PORT)"
else
    NEW_CONTAINER="campride-api-server-blue"
    NEW_SERVICE="app-blue"
    NEW_PORT=8080
    CURRENT_SERVICE="app-green"
    CURRENT_PORT=8081
    echo "Current container: $CURRENT_CONTAINER (Port: $CURRENT_PORT)"
    echo "New container: $NEW_CONTAINER (Port: $NEW_PORT)"
fi

# 새 버전의 라이브러리 유효성 검사
if [ ! -d "build/libs_new" ]; then
    echo "오류: build/libs_new 디렉토리가 존재하지 않습니다. 새 버전이 준비되지 않았습니다."
    exit 1
fi

# 새 컨테이너 시작
echo "Starting new container..."
if ! docker compose up -d --remove-orphans $NEW_SERVICE; then
    echo "오류: 새 컨테이너를 시작하지 못했습니다."
    exit 1
fi

# 새 컨테이너 헬스 체크
echo "Performing health check on new container..."
for i in $(seq 1 $MAX_RETRIES); do
    if curl -s "${HEALTH_CHECK_URL}:${NEW_PORT}/actuator/health" | grep -q "UP"; then
        echo "New version is healthy"

        # libs 디렉토리 처리
        if [ -d "build/libs" ]; then
            # libs_old 디렉토리가 존재하면 삭제
            if [ -d "build/libs_old" ]; then
                echo "Removing old libs backup..."
                rm -rf build/libs_old
            fi
            echo "Backing up current libs..."
            mv build/libs build/libs_old
        fi

        echo "Moving new libs to production..."
        mv build/libs_new build/libs

        # Nginx 설정 백업
        echo "Backing up Nginx configuration..."
        cp $NGINX_CONF "${NGINX_CONF}.bak"

        # Nginx 설정 업데이트
        echo "Updating Nginx configuration..."
        if [ -n "$CURRENT_CONTAINER" ]; then
            if ! sed -i "s|proxy_pass  http://$CURRENT_CONTAINER:$CURRENT_PORT|proxy_pass  http://$NEW_CONTAINER:$NEW_PORT|" $NGINX_CONF; then
                echo "Failed to update Nginx configuration!"
                echo "Restoring libs from backup..."
                rm -rf build/libs
                if [ -d "build/libs_old" ]; then
                    mv build/libs_old build/libs
                fi
                echo "Rolling back..."
                docker compose stop $NEW_SERVICE
                docker compose rm -f $NEW_SERVICE
                echo "Rolled back to $CURRENT_CONTAINER"
                exit 1
            fi
        else
            # 초기 배포인 경우 - 설정 파일에 proxy_pass 설정 추가
            echo "First deployment - adding proxy_pass configuration to Nginx..."
            # 실제 Nginx 설정 파일의 구조에 맞게 조정 필요
            if ! sed -i "s|# PROXY_PASS_PLACEHOLDER|proxy_pass  http://$NEW_CONTAINER:$NEW_PORT;|" $NGINX_CONF; then
                echo "Failed to update Nginx configuration for first deployment!"
                exit 1
            fi
        fi

        echo "Nginx configuration updated successfully"

        # Nginx 설정 리로드
        echo "Reloading Nginx configuration..."
        if ! docker compose exec -T nginx nginx -s reload; then
            echo "Failed to reload Nginx configuration! Reverting changes..."
            if [ -f "${NGINX_CONF}.bak" ]; then
                cp "${NGINX_CONF}.bak" $NGINX_CONF
                docker compose exec -T nginx nginx -s reload
            fi
            # 라이브러리 복원
            rm -rf build/libs
            if [ -d "build/libs_old" ]; then
                mv build/libs_old build/libs
            fi
            # 새 컨테이너 중지
            docker compose stop $NEW_SERVICE
            docker compose rm -f $NEW_SERVICE
            echo "Rolled back to $CURRENT_CONTAINER"
            exit 1
        fi

        echo "Switched traffic to $NEW_CONTAINER on port $NEW_PORT"

        # 이전 버전 컨테이너 중지 (초기 배포가 아닌 경우에만)
        if [ -n "$CURRENT_SERVICE" ]; then
            echo "Stopping previous container..."
            docker compose stop $CURRENT_SERVICE
            echo "Previous container stopped"
        fi

        echo "Deployment completed successfully at $(date)"
        exit 0
    fi

    echo "Attempt $i of $MAX_RETRIES: New version not healthy yet. Retrying in $RETRY_INTERVAL seconds..."
    sleep $RETRY_INTERVAL
done

# 헬스 체크 실패 시 롤백
echo "New version is not healthy. Rolling back..."

# Nginx 설정이 이미 변경되었는지 확인
if grep -q "$NEW_CONTAINER:$NEW_PORT" $NGINX_CONF; then
    echo "Reverting Nginx configuration..."
    sed -i "s|proxy_pass  http://$NEW_CONTAINER:$NEW_PORT|proxy_pass  http://$CURRENT_CONTAINER:$CURRENT_PORT|" $NGINX_CONF
    docker compose exec -T nginx nginx -s reload
fi

# 라이브러리가 이미 이동되었는지 확인
if [ ! -d "build/libs_new" ] && [ -d "build/libs_old" ]; then
    echo "Restoring libs from backup..."
    if [ -d "build/libs" ]; then
        rm -rf build/libs
    fi
    mv build/libs_old build/libs
fi

# 새 컨테이너 중지 및 제거
echo "Stopping and removing new container..."
docker compose stop $NEW_SERVICE
docker compose rm -f $NEW_SERVICE

echo "Rolled back to $CURRENT_CONTAINER at $(date)"
echo "배포 실패 로그는 $LOG_FILE 에서 확인할 수 있습니다."
exit 1