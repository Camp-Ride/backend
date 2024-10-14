#!/bin/bash

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
docker-compose up -d $NEW_CONTAINER_NAME

# 새 컨테이너가 준비될 때까지 대기
echo "Waiting for the new container to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:$NEW_PORT/actuator/health | grep -q "UP"; then
        echo "New container is ready"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "New container failed to start"
        exit 1
    fi
    sleep 10
done

# Nginx 설정 업데이트
NGINX_CONF="/home/ubuntu/develop/backend/data/nginx/nginx.conf"
sed -i "s/proxy_pass  http:\/\/$CURRENT_CONTAINER:$CURRENT_PORT/proxy_pass  http:\/\/$NEW_CONTAINER:$NEW_PORT/" $NGINX_CONF

# Nginx 설정 리로드
docker-compose exec -T nginx nginx -s reload

echo "Switched traffic to $NEW_CONTAINER on port $NEW_PORT"