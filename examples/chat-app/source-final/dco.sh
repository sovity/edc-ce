#!/usr/bin/env bash

set -eo pipefail
set -x

# Short hand for this split up docker compose
docker compose \
  -f docker-compose/edc-services.yaml \
  -f docker-compose/chat-app-services.yaml \
  $@

