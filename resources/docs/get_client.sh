#!/bin/sh

if [ ! $# -eq 1 ]
  then
    echo "Provide X509 keystore file as parameter, e.g. \"$ get_client.sh ./mycert.p12\""
    exit 1
fi

TEMP_FILE="./temp.cert"
if [ -n "$1" ]; then
    [ ! -f "$1" ] && (echo "Cert not found"; exit 1)
    openssl x509 -in "$1" -text > "$TEMP_FILE"
fi

SKI="$(grep -A1 "Subject Key Identifier"  "$TEMP_FILE" | tail -n 1 | tr -d ' ')"
AKI="$(grep -A1 "Authority Key Identifier"  "$TEMP_FILE" | tail -n 1 | tr -d ' ')"
echo "$SKI:$AKI"

rm $TEMP_FILE
