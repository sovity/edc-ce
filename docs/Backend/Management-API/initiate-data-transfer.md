## Initiating a Data-Transfer via the Management-API

In addition to the EDC UI, a data transfer can also be initiated via the Management-API.

### Required Knowledge

Initiating a data transfer via the Management-API requires some prior knowledge of what the Postman collection for your connector looks like and how to authenticate against it. Please familiarize yourself with the following documentation in advance:
- Connector Paths and Endpoints
- Management-API: Postman and Authorization

### Initiating the Data-Transfer as Consumer

To initiate a data transfer, an authenticated POST call must be made against the `transferprocesses` API with the following body, where the placeholders must be replaced with the specific data of the use case.

POST `{{Consumer-Management-API}}/v2/transferprocesses`

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
    "https://w3id.org/edc/v0.0.1/ns/assetId": "{{ASSET_ID}}",
    "https://w3id.org/edc/v0.0.1/ns/contractId": "{{CONTRACT_ID}}",
    "https://w3id.org/edc/v0.0.1/ns/connectorAddress": "{{PROVIDER_EDC_PROTOCOL_URL}}",
    "https://w3id.org/edc/v0.0.1/ns/connectorId": "{{PROVIDER_EDC_PARTICIPANT_ID}}",
    "https://w3id.org/edc/v0.0.1/ns/dataDestination": {
        "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
        "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{CONSUMER_EDC_TRANSFER_TARGET_URL}}"
    },
    "https://w3id.org/edc/v0.0.1/ns/properties": {},
    "https://w3id.org/edc/v0.0.1/ns/privateProperties": {},
    "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
    "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```
{% endcode %}

### Parameterized Data Sources

If the provider uses parameterized data sources, the consumer may need to provide additional information when initiating the data transfer. We have a separate documentation for parameterized data sources.
