How Can OAuth2 be used for Http-Data-Sources and Sinks?
========

Overview
========
OAuth2 protected APIs can be used for both Http-Data-Sources and Http-Data-Sinks. For both the
following properties can be used:

| Property            | Description                                                  |
|---------------------|--------------------------------------------------------------|
| oauth2:tokenUrl     | Token-Url where the Access-Token can be fetched from         |
| oauth2:clientId     | The client id                                                |
| oauth2:clientSecret | The secret of the client authenticating to the OAuth2-Server |

Provider
========

UI
--

To provide data from an oauth2 protected API using the EDC-Ui an asset with the
following `Custom Datasource Config (JSON)` can be created:

```json
{
  "properties": {
    "type": "HttpData",
    "baseUrl": "{{target-url}}",
    "oauth2:tokenUrl": "{{token-url}}",
    "oauth2:clientId": "{{client-id}}",
    "oauth2:clientSecret": "{{client-secret}}"
  }
}
```

API
---

To create an asset providing oauth2 protected data the management-API of the EDC can be used to send the
following request:

`POST` to `{{connector-base-url}}/control/data/assets`

```json
{
  "asset": {
    "properties": {
      "asset:prop:name": "test-asset",
      "asset:prop:curatorOrganizationName": "organization",
      "asset:prop:contenttype": "application/json",
      "asset:prop:description": "description",
      "asset:prop:datasource:http:hints:proxyBody": "false",
      "asset:prop:datasource:http:hints:proxyMethod": "false",
      "asset:prop:version": "1.0",
      "asset:prop:id": "urn:artifact:test-asset:1.0",
      "asset:prop:datasource:http:hints:proxyPath": "false",
      "asset:prop:datasource:http:hints:proxyQueryParams": "false",
      "asset:prop:originator": "{{originator-base-path}}/control/api/v1/ids/data",
      "asset:prop:language": "https://w3id.org/idsa/code/EN"
    }
  },
  "dataAddress": {
    "properties": {
      "type": "HttpData",
      "baseUrl": "{{target-url}}",
      "oauth2:tokenUrl": "{{token-url}}",
      "oauth2:clientId": "{{client-id}}",
      "oauth2:clientSecret": "{{client-secret}}"
    }
  }
}
```

Consumer
========

UI
--

To start a transfer to an oauth2 protected API using the EDC-Ui a transfer with the
following `Custom Datasink Config (JSON)` type can be started:

```json
{
  "properties": {
    "type": "HttpData",
    "baseUrl": "{{target-url}}",
    "oauth2:tokenUrl": "{{token-url}}",
    "oauth2:clientId": "{{client-id}}",
    "oauth2:clientSecret": "{{client-secret}}"
  }
}
```

API
---

To start a transfer to an oauth2 protected API the management-API of the EDC can be used to send the
following request:

`POST` to `{{connector-base-url}}/control/data/transferprocess`

```json
{
  "protocol": "ids-multipart",
  "assetId": "urn:artifact:http",
  "contractId": "{{contract-id}}",
  "dataDestination": {
    "properties": {
      "type": "HttpData",
      "baseUrl": "{{target-url}}",
      "oauth2:tokenUrl": "{{token-url}}",
      "oauth2:clientId": "{{client-id}}",
      "oauth2:clientSecret": "{{client-secret}}"
    }
  },
  "transferType": {
    "contentType": "application/octet-stream",
    "isFinite": true
  },
  "managedResources": false,
  "connectorAddress": "https://{{providerConnectorUrl}}/api/v1/ids/data",
  "connectorId": "consumer"
}
```
