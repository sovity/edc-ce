Data Sources and Data Sinks protected by OAuth2
========

> [!NOTE]
> This feature is only available in our sovity EDC Enterprise Edition.

## Overview

OAuth2 protected APIs can be used for both Http-Data-Sources and Http-Data-Sinks. For both the
following properties can be used:

| Property            | Description                                                  |
|---------------------|--------------------------------------------------------------|
| oauth2:tokenUrl     | Token-Url where the Access-Token can be fetched from         |
| oauth2:clientId     | The client id                                                |
| oauth2:clientSecret | The secret of the client authenticating to the OAuth2-Server |

> [!NOTE]
> The only supported flow right now is the "Client Credentials" flow.

## Data Sources secured via OAuth2

### Providing the Asset via the UI

To provide data from an oauth2 protected API using the EDC-Ui an asset with the
following `Custom Datasource Config (JSON)` can be created:

```json
{
  "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
  "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}",
  "oauth2:tokenUrl": "{{token-url}}",
  "oauth2:clientId": "{{client-id}}",
  "oauth2:clientSecret": "{{client-secret}}"
}
```

### Providing the Asset via the Management API

To create an asset providing oauth2 protected data the management-API of the EDC can be used to send the
following request:

`POST` to `https://{{FQDN}}/api/management/v2/assets`

```json
{
  "@type": "https://w3id.org/edc/v0.0.1/ns/Asset",
  "https://w3id.org/edc/v0.0.1/ns/properties": {
    "https://w3id.org/edc/v0.0.1/ns/id": "my-asset-1.0",
    "http://www.w3.org/ns/dcat#version": "1.0",
    "http://purl.org/dc/terms/language": "https://w3id.org/idsa/code/EN",
    "http://purl.org/dc/terms/title": "test-document",
    "http://purl.org/dc/terms/description": "my test document",
    "http://www.w3.org/ns/dcat#keyword": [
      "keyword1",
      "keyword2"
    ],
    "http://purl.org/dc/terms/creator": {
      "http://xmlns.com/foaf/0.1/name": "My Org"
    },
    "http://purl.org/dc/terms/license": "https://creativecommons.org/licenses/by/4.0/",
    "http://www.w3.org/ns/dcat#landingPage": "https://mydepartment.myorg.com/my-offer",
    "http://www.w3.org/ns/dcat#mediaType": "text/plain",
    "https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyMethod": "false",
    "https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyPath": "false",
    "https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyQueryParams": "false",
    "https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyBody": "false",
    "http://purl.org/dc/terms/publisher": {
      "http://xmlns.com/foaf/0.1/homepage": "https://myorg.com/"
    }
  },
  "https://w3id.org/edc/v0.0.1/ns/privateProperties": {},
  "https://w3id.org/edc/v0.0.1/ns/dataAddress": {
    "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}",
    "oauth2:tokenUrl": "{{token-url}}",
    "oauth2:clientId": "{{client-id}}",
    "oauth2:clientSecret": "{{client-secret}}"
  }
}
```

## Data Sinks secured by OAuth2

### Initiating the Transfer via the UI

To start a transfer to an oauth2 protected API using the EDC-Ui a transfer with the
following `Custom Datasink Config (JSON)` type can be started:

```json
{
  "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
  "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}",
  "oauth2:tokenUrl": "{{token-url}}",
  "oauth2:clientId": "{{client-id}}",
  "oauth2:clientSecret": "{{client-secret}}"
}
```

### Initiating the Transfer via the Management API

To start a transfer to an oauth2 protected API the management-API of the EDC can be used to send the
following request:

`POST` to `https://{{FQDN}}/api/management/v2/transferprocesses`

```json
{
  "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
  "https://w3id.org/edc/v0.0.1/ns/assetId": "{{ASSET_ID}}",
  "https://w3id.org/edc/v0.0.1/ns/contractId": "{{CONTRACT_ID}}",
  "https://w3id.org/edc/v0.0.1/ns/connectorAddress": "https://{{PROVIDER_EDC_FQDN}}/api/dsp",
  "https://w3id.org/edc/v0.0.1/ns/connectorId": "{{PROVIDER_EDC_PARTICIPANT_ID}}",
  "https://w3id.org/edc/v0.0.1/ns/dataDestination": {
    "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}",
    "oauth2:tokenUrl": "{{token-url}}",
    "oauth2:clientId": "{{client-id}}",
    "oauth2:clientSecret": "{{client-secret}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/properties": {},
  "https://w3id.org/edc/v0.0.1/ns/privateProperties": {},
  "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
  "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```
