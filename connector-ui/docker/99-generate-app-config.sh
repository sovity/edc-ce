#!/bin/sh

set -e

# Pass all EDC_UI_ properties to the UI
jq -n 'env | with_entries( select(.key | startswith("EDC_UI_") ) )' > /tmp/app-configuration.json
