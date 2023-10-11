How Can a Pull-Data-Transfer Be Performed?
========

Overview
========

The following diagram describes the sequence of actions involved in a Pull-Data-Transfer:

![pull-data-transfer.png](images%2Fpull-data-transfer.png)

Provider
========
Steps:
1.  Create an `Rest-Api-Endpoint` (Technically a HttpData)-Type Asset
2.  Create a Policy
3.  Create a ContractDefinition

Consumer
========
Steps:
1. Prepare a `Pulling-Backend`
2. Negotiate a Contract with the Provider
3. Start a Data-Transfer using the `Json-Type`

Preparing the Pulling-Backend
-----------------------------

The `Pulling-Backend` is involved in steps 3, 4 and 11 of the diagram. It should provide an endpoint for receiving information regarding the `Data-Transfer-Request` (3). These information can then be used to start the `Pull-Data-Transfer` (4) and receiving the provider data (11).

The current EDC-Implementation does not support dynamically setting the url of the Pulling-Backend. As a result sovity has to set the endpoint for the backend manually. Please provide us the corresponding endpoint.

Provide an EndpointDataReference Endpoint (4)
---------------------------------------------
The pulling backend should provide an endpoint to accept the following JSON using a POST Request.

Header: Content-Type: `application/json`, Authorization: `{{Auth-Key}}`

Body:
```json
{
  "id": "2d5348ea-b1e0-4b69-a625-07e7b093944a",
  "endpoint": "http://connector-a-dataplane-1:8185/public",
  "authKey": "Authorization",
  "authCode": "Token",
  "properties": {
    "cid": "cd:30c40c02-2e8f-4f16-8790-57e517d8b8ab"
  }
}
```

To pull data a http request to the provided `{{endpoint}}` using the header: `{{authKey}}: {{authCode}}` should be started.

Starting the Data-Transfer using the Connector UI
-------------------------------------------------

To trigger a Pull-Data-Transfer, please select "Custom Transfer Process JSON" when initiating the transfer, and provide:
```json
{
  "@type": "https://w3id.org/edc/v0.0.1/ns/TransferRequest",
  "https://w3id.org/edc/v0.0.1/ns/dataDestination": {
    "https://w3id.org/edc/v0.0.1/ns/type": "HttpProxy",
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/properties": {
    "https://w3id.org/edc/v0.0.1/ns/receiverHttpEndpoint": "{{target-pull-backend-url}}",
  },
  "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
  "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```

Starting the Data-Transfer using the EDC-Api
-------------------------------------------------

To start a pull-http-transfer using the management-API of the EDC one can send the following request:

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
  "https://w3id.org/edc/v0.0.1/ns/properties": {
    "https://w3id.org/edc/v0.0.1/ns/receiverHttpEndpoint": "{{target-pull-backend-url}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
  "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```
