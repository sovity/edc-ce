#!/bin/bash

if [ ! $# -eq 2 ]
  then
    echo "Provide X509 keystore file as parameter along keystore's password, e.g. \"$ generate_ski_aki.sh ./mycert.p12 PASSWORD\""
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
    if [[ $(openssl version) == *"1.1.1"* ]];
    then
        openssl pkcs12 -in "$P12_FILE" -clcerts -nokeys -out "$CRT_FILE" -passin "pass:$PASSWORD"
    else
        openssl pkcs12 -in "$P12_FILE" -clcerts -nokeys -legacy -out "$CRT_FILE" -passin "pass:$PASSWORD"
    fi
    openssl x509 -in "$CRT_FILE" -text > "$TEMP_FILE"
    keytool -importkeystore -srckeystore "$P12_FILE" -srcstoretype pkcs12 -destkeystore "$JKS_FILE" -deststoretype jks -deststorepass "$PASSWORD" -srcstorepass "$PASSWORD" -noprompt 2>/dev/null
fi

SKI="$(grep -A1 "Subject Key Identifier"  "$TEMP_FILE" | tail -n 1 | tr -d ' ')"
AKI="$(grep -A1 "Authority Key Identifier"  "$TEMP_FILE" | tail -n 1 | tr -d ' ')"

if [[ $AKI == "keyid"* ]]; then
	echo "$SKI:$AKI"
else
	echo "$SKI:keyid:$AKI"
fi

rm "$TEMP_FILE"
rm "$CRT_FILE"