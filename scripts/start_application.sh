#!/bin/bash
set -e

echo "Starting new version"
cd /home/ubuntu/develop/backend

# 현재 실행 중인 컨테이너 확인
CURRENT_CONTAINER=$(docker ps --filter "name=campride-api-server" --format "{{.Names}}")
if [ "$CURRENT_CONTAINER" == "campride-api-server-blue" ]; then
    NEW_CONTAINER="campride-api-server-green"
    NEW_CONTAINER_NAME="app-green"
    NEW_PORT=8081
    CURRENT_PORT=8080
else
    NEW_CONTAINER="campride-api-server-blue"
    NEW_CONTAINER_NAME="app-blue"
    NEW_PORT=8080
    CURRENT_PORT=8081
fi

# 새 컨테이너 시작
docker compose up -d $NEW_CONTAINER_NAME

echo "New container: $NEW_CONTAINER"
echo "New container name: $NEW_CONTAINER_NAME"
echo "New container port: $NEW_PORT"

# 새 컨테이너가 준비될 때까지 대기
echo "Waiting for the new container to be ready..."
for i in {1..10}; do
    if curl -s http://localhost:$NEW_PORT/actuator/health | grep -q "UP"; then
        echo "New version is healthy"
        # 새 버전의 라이브러리를 메인 디렉토리로 이동
        mv build/libs build/libs_old
        mv build/libs_new build/libs
        # 이전 버전 컨테이너 중지
        docker compose stop $OLD_SERVICE
        break
    fi
    if [ $i -eq 10 ]; then
        echo "New version is not healthy. Rolling back."

        # 롤백
        NGINX_CONF="/home/ubuntu/develop/backend/data/nginx/nginx.conf"
        sed -i "s/proxy_pass  http:\/\/$NEW_CONTAINER:8080/proxy_pass  http:\/\/$OLD_CONTAINER:8080/" $NGINX_CONF
        docker compose exec -T nginx nginx -s reload
        docker compose stop $NEW_SERVICE
        docker compose start $OLD_SERVICE
        echo "Rolled back to $OLD_CONTAINER"
        exit 1
    fi
    sleep 10
done

# Nginx 설정 업데이트
NGINX_CONF="/home/ubuntu/develop/backend/data/nginx/nginx.conf"
sed -i "s/proxy_pass  http:\/\/$CURRENT_CONTAINER:$CURRENT_PORT/proxy_pass  http:\/\/$NEW_CONTAINER:$NEW_PORT/" $NGINX_CONF

# Nginx 설정 리로드
docker compose exec -T nginx nginx -s reload

echo "Switched traffic to $NEW_CONTAINER on port $NEW_PORT"