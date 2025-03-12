#!/bin/bash

# 현재 스크립트가 위치한 디렉토리로 이동 (어디서 실행해도 정상 동작)
cd "$(dirname "$0")"

# 현재 실행 중인 컨테이너 종료
echo "🔨 compose 종료..."
docker compose down || { echo "❌ compose 종료 실패!"; exit 1; }

# start.sh 실행
echo "🔨 compose 재실행..."
./start.sh


# 실행 전  권한 부여 필요
# chmod +x restart.sh

# 실행 cmd
# ./restart.sh