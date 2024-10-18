#!/bin/bash

echo "Preparing for new deployment"
cd /home/ubuntu/develop/backend
git pull
mkdir -p build/libs_new

# Nginx 설정 파일 및 디렉토리 권한 조정
sudo chown -R ubuntu:ubuntu /home/ubuntu/develop/backend/data/nginx
sudo chmod 755 /home/ubuntu/develop/backend/data/nginx
sudo chmod 644 /home/ubuntu/develop/backend/data/nginx/nginx.conf