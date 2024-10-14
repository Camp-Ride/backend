echo "Validating new deployment"
cd /home/ubuntu/develop/backend

# 현재 활성 컨테이너 확인
NEW_CONTAINER=$(docker ps --filter "name=campride-api-server" --format "{{.Names}}")
OLD_CONTAINER=$(docker ps -a --filter "name=campride-api-server" --filter "status=exited" --format "{{.Names}}")

# 새 버전 상태 확인
for i in {1..5}; do
    response=$(curl -sS http://localhost:8080/actuator/health)
    if [[ $response == *"UP"* ]]; then
        echo "New version is healthy"
        # 새 버전의 라이브러리를 메인 디렉토리로 이동
        mv build/libs build/libs_old
        mv build/libs_new build/libs
        # 이전 버전 컨테이너 중지
        docker-compose stop $OLD_CONTAINER
        exit 0
    fi
    sleep 10
done

echo "New version is not healthy. Rolling back."

# 롤백
NGINX_CONF="/home/ubuntu/develop/backend/data/nginx/nginx.conf"
sed -i "s/proxy_pass  http:\/\/$NEW_CONTAINER:8080/proxy_pass  http:\/\/$OLD_CONTAINER:8080/" $NGINX_CONF
docker-compose exec -T nginx nginx -s reload
docker-compose stop $NEW_CONTAINER
docker-compose start $OLD_CONTAINER
echo "Rolled back to $OLD_CONTAINER"
exit 1