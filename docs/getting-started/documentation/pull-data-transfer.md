Consuming Data via HttpProxy / HTTP Pull
========

> [!WARNING]
> This feature is only available for our sovity EDC Enterprise Edition.

## Overview

The following diagram describes the sequence of actions involved in a Pull-Data-Transfer (blue lines):

![data-transfer-methods](images/data-transfer-methods.png)

The Use Case Application is involved in steps b1, b4, b5 and b8 of the diagram. It should provide an endpoint for receiving
the EDR (b4). These information can then be used to start the tranfser request (b5). The result of the transfer request
will contain the data (b8).

## Requirements

- An active contract agreement for a data offer you want to consume.
- A Use Case Application / Pull Backend that can be reached from the EDC, and that can reach the Data Planes of that
  EDC.

## Initiating the Transfer

For the EDC send an EDR to your backend application, you need to initiate a transfer process.

This "transfer process" represents the lifetime of your EDR in which your backend application can initiate as many
transfers as it wants, using the EDR it has received.

### Initiating the Transfer via the UI

When initiating the transfer, select `Custom Transfer Process Request (JSON)`, and provide:

```json
{
  "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
  "https://w3id.org/edc/v0.0.1/ns/dataDestination": {
    "https://w3id.org/edc/v0.0.1/ns/type": "HttpProxy"
  },
  "https://w3id.org/edc/v0.0.1/ns/privateProperties": {
    "https://w3id.org/edc/v0.0.1/ns/receiverHttpEndpoint": "{{TARGET_PULL_BACKEND_URL}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
  "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```

### Initiating the Transfer via the Management API

`POST` to `https://{{FQDN}}/api/management/v2/transferprocesses`

```json
{
  "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
  "https://w3id.org/edc/v0.0.1/ns/assetId": "{{ASSET_ID}}",
  "https://w3id.org/edc/v0.0.1/ns/contractId": "{{CONTRACT_ID}}",
  "https://w3id.org/edc/v0.0.1/ns/connectorAddress": "https://{{PROVIDER_EDC_FQDN}}/api/dsp",
  "https://w3id.org/edc/v0.0.1/ns/connectorId": "{{PROVIDER_EDC_PARTICIPANT_ID}}",
  "https://w3id.org/edc/v0.0.1/ns/dataDestination": {
    "https://w3id.org/edc/v0.0.1/ns/type": "HttpProxy",
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/privateProperties": {
    "https://w3id.org/edc/v0.0.1/ns/receiverHttpEndpoint": "{{target-pull-backend-url}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
  "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```

## Receiving an Endpoint Data Reference (EDR)

Your backend receives the EDR from the EDC by the EDC calling the `{{target-pull-backend-url}}` endpoint via `POST` method:
```json
{
  "id": "2d5348ea-b1e0-4b69-a625-07e7b093944a",
  "endpoint": "http://connector-a-dataplane-1:8185/public",
  "authKey": "Authorization",
  "authCode": "Token ..."
}
```

## Getting the Data

Using that EDR, requesting `GET` on the EDR's `{{ endpoint }}` using the header `{{ authKey }}: {{ authCode }}`
will return the data.

### Accessing the Contract ID

The `authCode` JWT Token can be decoded to find the Contract Agreement ID.

### Parameterized HTTP Data Sources

- When method proxying is enabled on the providing side, the request method can be adjusted and will be used by the
  providing EDC when fetching data from the data source.
- When path proxying is enabled on the providing side, any appended path to the `{{ endpoint }}` will be proxied through
  to the data source.
- When query params proxying is enabled on the providing side, added query params will be passed through to the data
  source.
- When request body proxying is enabled on the providing side, the request body and content-type headers will be proxied
  to the provider side.

