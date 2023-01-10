#!/bin/bash

if [ ! $# -eq 2 ]
  then
    echo "Provide X509 keystore file as parameter along keystore's password, e.g. \"$ get_client.sh ./mycert.p12 PASSWORD\""
    exit 1
fi

P12_FILE=$1
PASSWORD=$2
P12_ENDING=".p12"
JKS_ENDING=".jks"

JKS_FILE=${P12_FILE//".p12"/".jks"}
TEMP_FILE="./temp.cert"
CRT_FILE="./cert.crt"



if [ -n "$P12_FILE" ]; then
    [ ! -f "$P12_FILE" ] && (echo "Cert not found"; exit 1)
    openssl pkcs12 -in "$P12_FILE" -clcerts -nokeys -out "$CRT_FILE"
    openssl x509 -in "$CRT_FILE" -text > "$TEMP_FILE"
    keytool -importkeystore -srckeystore "$P12_FILE" -srcstoretype pkcs12 -destkeystore "$JKS_FILE" -deststoretype jks -deststorepass "$PASSWORD"
fi

SKI="$(grep -A1 "Subject Key Identifier"  "$TEMP_FILE" | tail -n 1 | tr -d ' ')"
AKI="$(grep -A1 "Authority Key Identifier"  "$TEMP_FILE" | tail -n 1 | tr -d ' ')"
echo "$SKI:$AKI"

rm "$TEMP_FILE"
rm "$CRT_FILE"