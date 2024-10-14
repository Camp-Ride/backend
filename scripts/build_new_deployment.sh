#!/bin/bash

echo "Building new version deployment"
cd /home/ubuntu/develop/backend
cp -R /home/ubuntu/develop/backend/build/libs/* build/libs_new/

# 현재 실행 중인 컨테이너 확인
CURRENT_CONTAINER=$(docker ps --filter "name=campride-api-server" --format "{{.Names}}")
echo "Current container: $CURRENT_CONTAINER"
if [ "$CURRENT_CONTAINER" == "campride-api-server-blue" ]; then
    NEW_CONTAINER="campride-api-server-green"
else
    NEW_CONTAINER="campride-api-server-blue"
fi

# 새 버전의 이미지 빌드
docker compose build $NEW_CONTAINER