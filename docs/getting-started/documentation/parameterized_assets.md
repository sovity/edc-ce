How to share parameterized HTTP data sources to expose entire APIs
========

Provider: Create Asset (EDC UI)
========
Create a `Custom Datasource Config (JSON)` asset over the edc-ui using the following Json:
```json
{
    "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{DATA_SOURCE_URL}}",
    "https://w3id.org/edc/v0.0.1/ns/proxyPath": "true",
    "https://w3id.org/edc/v0.0.1/ns/proxyBody": "true",
    "https://w3id.org/edc/v0.0.1/ns/proxyMethod": "true",
    "https://w3id.org/edc/v0.0.1/ns/proxyQueryParams": "true"
}
```

Note: Proxy-Parameters are optional, you can use any combination of them. If you don't want to use a speceific Proxy-Parameter you can simply remove the whole line.

Consumer: Start Transfer (Management-API)
========
Start a transfer using the Management API using the following JSON:

The relevant fields for API parametrization are located in the dataDestination section of the transfer process.

Disclaimer: This is only working in the sovity EDC in combination with sovity EDC variants.

Note: When using the body-parameterization a mediaType must also be specified.

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
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{DATA_SINK_URL}}",
    "https://sovity.de/workaround/proxy/param/pathSegments": "{{PARAMETERIZATION_PATH}}",
    "https://sovity.de/workaround/proxy/param/method": "{{PARAMETERIZATION_METHOD}}",
    "https://sovity.de/workaround/proxy/param/queryParams": "{{PARAMETERIZATION_QUERY}}",
    "https://sovity.de/workaround/proxy/param/mediaType": "{{PARAMETERIZATION_CONTENTTYPE}}",
    "https://sovity.de/workaround/proxy/param/body": "{{PARAMETERIZATION_BODY}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/privateProperties": {},
  "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
  "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```
