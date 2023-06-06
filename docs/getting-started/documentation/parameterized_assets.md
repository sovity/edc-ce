How to Enable Consumers Parameterized Data Source APIs
========

Provider Asset
========

Create an `Custom Datasource Config (JSON)` asset over the edc-ui using the following Json:
```json
{
  "properties": {
    "type": "HttpData",
    "baseUrl": "https://google.de",
    "proxyPath": "true",
    "proxyBody": "true",
    "proxyMethod": "true",
    "proxyQueryParams": "true"
  }
}
```

Start Transfer
========

Start a transfer using the `transferprocess`-POST-Endpoint using the following JSON.
Make sure to update the `contractId` and `dataDestination`.
The relevant fields for API parametrization are located in the properties of the `transferProcessDTO` (not in `dataDestination`).
In this example `pathSegments`, `method` and `queryParams` are passed.
```json
{
  "protocol": "ids-multipart",
  "assetId": "urn:artifact:google",
  "contractId": "cd:8495f437-a38c-40b9-a653-7e64d2cf6b08",
  "dataDestination": {
    "properties": {
      "type": "HttpData",
      "assetId": "urn:artifact:google",
      "baseUrl": "https://webhook.site/6eddc60d-e863-41c3-9f0b-2cb4045977a5"
    }
  },
  "properties": {
    "pathSegments": "search",
    "method": "GET",
    "queryParams": "q=sovity"
  },
  "transferType": {
    "contentType": "application/octet-stream",
    "isFinite": true
  },
  "managedResources": false,
  "connectorAddress": "http://provider-connector-controlplane-1:8282/api/v1/ids/data",
  "connectorId": "consumer"
}
```
