#!/bin/sh

set -e

jq -n 'env | with_entries( select(.key | startswith("EDC_UI_") ) )' > /tmp/app-config.json
