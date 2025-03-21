## Overview

The Connector also supports communication with protected data-sources and data-sinks. The following combinations are possible: 
- **Data-Source**: OAuth2 + API-Key protected APIs for HttpData.
- **Data-Sink**: Only API-Keys can be used for HttpData.

The following properties can be used within the `dataAddress` array or respectively the `dataDestination` array.

### OAuth 2.0

{% hint style="info" %} In the newest Connector versions, you are able to manage the secrets yourself via the Management-API. If you are not yet on the newest version and want to use OAuth 2.0, please create a Ticket in the Service Desk. We need to add your secret combined with a secret-alias to our vault; the secret-alias can then be used as the value of `clientSecretKey`. {% endhint %}

- `oauth2:tokenUrl` -> token URL where the access-token can be fetched from
- `oauth2:clientId` -> the client ID
- `oauth2:clientSecretKey` -> the secret-alias in our vault holding the secret (see underlined note)
- `oauth2:scope` -> (optional) the requested scope

The only supported OAuth2 flow right now is the Client Credentials flow.

### API-Keys

- `authKey` -> optional authentication header, e.g., ```X-Api-Key```
- `authCode` -> optional authentication value, such as the actual API key

### Data-Sources

The data-source settings must be set when the asset is created. 

#### Via the UI

To provide data from an OAuth2 protected API using the EDC UI, an asset with the following Custom Datasource Config (JSON) can be created, which is essentially equal to the `dataAddress` array for the Management-API (see below):

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
  "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}",
  "oauth2:tokenUrl": "{{token-url}}",
  "oauth2:clientId": "{{client-id}}",
  "oauth2:clientSecretKey": "{{client-secret-key}}"
}
```
{% endcode %}

#### Via the Management-API

To create an asset providing OAuth2 protected data, the Management-API of the EDC can be used to send the following request:

POST to `/v3/assets`

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
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
    "oauth2:clientSecretKey": "{{client-secret-key}}"
  }
}
```
{% endcode %}

### Data Sinks

The data-sink settings must be entered when initiating the transfer. 

#### Initiating Transfer via UI

To start a transfer to an API-Key protected API using the EDC UI, a transfer with the following Custom Datasink Config (JSON) type can be started, which is basically equal to the `dataDestination` array in the body for the Management-API:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
  "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}",
  "authKey": "{{authKey}}",
  "authCode": "{{authCode}}"
}
```
{% endcode %}

#### Initiating Transfer via Management-API

To start a transfer to an API-Key protected API, the Management-API of the EDC can be used to send the following request:

POST `to /v3/transferprocesses`

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
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
    "authKey": "{{authKey}}",
    "authCode": "{{authCode}}"
  },
  "https://w3id.org/edc/v0.0.1/ns/properties": {},
  "https://w3id.org/edc/v0.0.1/ns/privateProperties": {},
  "https://w3id.org/edc/v0.0.1/ns/protocol": "dataspace-protocol-http",
  "https://w3id.org/edc/v0.0.1/ns/managedResources": false
}
```
{% endcode %}

### Additional Header

Additional individual headers for the data-source or data-sink can be added as desired in the corresponding areas of the `dataAddress` (data-source) and/or `dataDestination` (data-sink) of the API-calls. To do this, the following combination must be inserted in the appropriate places in the API-Calls:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
"header:{{yourHeaderIdentifier}}": "{{yourHeaderValue}}"
```
{% endcode %}

Example for a header `hello` with value `world` within the dataDestination towards the data-sink:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
  "https://w3id.org/edc/v0.0.1/ns/dataDestination": {
    "https://w3id.org/edc/v0.0.1/ns/type": "HttpData",
    "https://w3id.org/edc/v0.0.1/ns/baseUrl": "{{target-url}}",
    "header:hello": "world"
  }
```
{% endcode %}

### Note: HttpData-Pull
Currently, with the HttpData-Pull EDR-flow and the use of API-keys, these API-keys cannot be added dynamically when initiating the transfer. For this, our infrastructure team must store the desired combination of target URL and API-key (auth-key and auth-code) for your Connector in our infrastructure. Please contact our support team to do this in advance.

{% hint style="info" %} 
Link: <a href="https://sovity.zammad.com/#ticket/view/my_tickets">Service Desk</a>
{% endhint %}
