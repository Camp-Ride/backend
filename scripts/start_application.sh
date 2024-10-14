#!/bin/bash
set -e

echo "Starting new version deployment"
cd /home/ubuntu/develop/backend

NGINX_CONF="/home/ubuntu/develop/backend/data/nginx/nginx.conf"
HEALTH_CHECK_URL="http://localhost:"
MAX_RETRIES=10
RETRY_INTERVAL=10

# 현재 실행 중인 컨테이너 확인 및 새 컨테이너 설정
CURRENT_CONTAINER=$(docker ps --filter "name=campride-api-server" --format "{{.Names}}")
if [ "$CURRENT_CONTAINER" == "campride-api-server-blue" ]; then
    NEW_CONTAINER="campride-api-server-green"
    NEW_SERVICE="app-green"
    NEW_PORT=8081
    CURRENT_SERVICE="app-blue"
    CURRENT_PORT=8080
else
    NEW_CONTAINER="campride-api-server-blue"
    NEW_SERVICE="app-blue"
    NEW_PORT=8080
    CURRENT_SERVICE="app-green"
    CURRENT_PORT=8081
fi

echo "Current container: $CURRENT_CONTAINER (Port: $CURRENT_PORT)"
echo "New container: $NEW_CONTAINER (Port: $NEW_PORT)"

# 새 컨테이너 시작
echo "Starting new container..."
docker compose up -d --remove-orphans $NEW_SERVICE

# 새 컨테이너 헬스 체크
echo "Performing health check on new container..."
for i in $(seq 1 $MAX_RETRIES); do
    if curl -s "${HEALTH_CHECK_URL}${NEW_PORT}/actuator/health" | grep -q "UP"; then
        echo "New version is healthy"
        # 새 버전의 라이브러리를 메인 디렉토리로 이동

        # libs_old 디렉토리가 존재하면 삭제
        if [ -d "build/libs_old" ]; then
            rm -rf build/libs_old
        fi

        mv build/libs build/libs_old
        mv build/libs_new build/libs

        # Nginx 설정 업데이트
        sed -i "s/proxy_pass  http:\/\/$CURRENT_CONTAINER:$CURRENT_PORT/proxy_pass  http:\/\/$NEW_CONTAINER:$NEW_PORT/" $NGINX_CONF


        # Nginx 설정 리로드
        docker compose exec -T nginx nginx -s reload

        echo "Switched traffic to $NEW_CONTAINER on port $NEW_PORT"

        # 이전 버전 컨테이너 중지
        docker compose stop $CURRENT_SERVICE

        echo "Deployment completed successfully"
        exit 0
    fi

    echo "Attempt $i of $MAX_RETRIES: New version not healthy yet. Retrying in $RETRY_INTERVAL seconds..."
    sleep $RETRY_INTERVAL
done

# 헬스 체크 실패 시 롤백
echo "New version is not healthy. Rolling back..."

# 새 컨테이너 중지 및 제거
docker compose stop $NEW_SERVICE
docker compose rm -f $NEW_SERVICE

echo "Rolled back to $CURRENT_CONTAINER"
exit 1