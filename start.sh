#!/bin/bash

# 현재 스크립트가 위치한 디렉토리로 이동 (어디서 실행해도 정상 동작)
cd "$(dirname "$0")"

echo "🔨 Gradle Build 시작..."
./gradlew clean build -x test || { echo "❌ Gradle Build 실패!"; exit 1; }

echo "🚀 Docker Compose 실행..."
docker compose up -d --build

echo "📋 Docker compose ps 실행..."
docker compose ps

# 실행 전  권한 부여 필요
# chmod +x start.sh

# 실행
# ./start.sh