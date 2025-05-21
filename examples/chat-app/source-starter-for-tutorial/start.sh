#!/usr/bin/env bash

set -eo pipefail
set -x

# Trap function to handle Ctrl+C
cleanup() {
    echo "Shutting down containers..."
    ./dco.sh down -v -t 1
    echo "Shutdown complete"
    exit 0
}
trap cleanup INT TERM
./dco.sh down -v -t 1

# Build Backend
(cd backend && ./gradlew clean build -x test)

# Build Frontend
(cd frontend && {

  # Install dependencies
  echo "Installing dependencies..."
  corepack enable
  yarn install

  # Build the production version
  echo "Building production version..."
  yarn build-prod

  # Docker image will be built by docker-compose
})

# Start Docker Compose
./dco.sh pull
./dco.sh up -d --build --remove-orphans
./dco.sh logs -f provider consumer provider-chat-app-backend consumer-chat-app-backend provider-chat-app-frontend consumer-chat-app-frontend
