How to share parameterized HTTP data sources to expose entire APIs
========

Provider Asset
========
Create an `Custom Datasource Config (JSON)` asset over the edc-ui using the following Json:
```json
{
    "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "https://app.mydepartment.myorg.com/api",
    "https://w3id.org/edc/v0.0.1/ns/proxyPath": "true",
    "https://w3id.org/edc/v0.0.1/ns/proxyBody": "true",
    "https://w3id.org/edc/v0.0.1/ns/proxyMethod": "true",
    "https://w3id.org/edc/v0.0.1/ns/proxyQueryParams": "true"
}
```

Start Transfer
========
Start a transfer using the Management API using the following JSON:

The relevant fields for API parametrization are located in the properties section of the transfer process, 
not the data address of the data sink.

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
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/privateProperties": {
    "https://w3id.org/edc/v0.0.1/ns/pathSegments": "my-endpoint",
    "https://w3id.org/edc/v0.0.1/ns/method": "POST",
    "https://w3id.org/edc/v0.0.1/ns/queryParams": "filter=abc&limit=10",
    "https://w3id.org/edc/v0.0.1/ns/contentType": "application/json",
    "https://w3id.org/edc/v0.0.1/ns/body": "{\"myBody\": \"myValue\"}"
  },
  "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
  "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```
