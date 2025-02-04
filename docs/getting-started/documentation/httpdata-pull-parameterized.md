## Parameterized Data-Transfer using HttpData-Pull and EDR

### Overview

In certain scenarios, it is beneficial to be able to provide multiple datasets and data-source APIs through a single asset.
This approach enhances the scalability of the Connector by minimizing the number of contract negotiations and reducing the catalog size.

### Providing Data

In this example, multiple datasets of the same API are identified using a unique path parameter but having the same base-url:
- `https://example.com/dataset/1`
- `https://example.com/dataset/2`
- `https://example.com/dataset/3`
- `https://example.com/dataset/4`

This implies that the `baseUrl` of all datasets is `https://example.com/dataset/`.

#### Defining the Asset in the EDC

To enable this example by using a single asset, use the following way to create an asset in the EDC:

`POST {{MANAGEMENT-API}}/v3/assets`

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "edc": "https://w3id.org/edc/v0.0.1/ns/",
        "cx-common": "https://w3id.org/catenax/ontology/common#",
        "cx-taxo": "https://w3id.org/catenax/taxonomy#",
        "dct": "http://purl.org/dc/terms/"
    },
    "properties": {
        "http://purl.org/dc/terms/title": "example-asset",
        "http://purl.org/dc/terms/description": "Asset providing access to multiple datasets",
        "edc:id": "example-asset",
        "cx-common:version": "3.0"
    },
    "dataAddress": {
        "@type": "DataAddress",
        "type": "HttpData",
        "baseUrl": "https://example.com/dataset/",
        "proxyPath": "true",
        "proxyQueryParams": "true"
    }
}
```
{% endcode %}

Ensure that `proxyPath` and `proxyQueryParams` are correctly set and enabled `true`.

### Consuming Data

#### Querying the Catalog

To access the dataset, query the provider's EDC catalog and identify the required asset. Extract the following details:
- `dcat:dataset.{asset}.odrl:hasPolicy.@id` - The data offer ID, later to be used for `{{data-offer-id}}`
- `dcat:dataset.{asset}.odrl:hasPolicy.odrl:permission` - The policies in this case permissions, needed to start the negotiation, later used for `{{permissions}}`

#### Negotiating the EDR Token

Next, request the `EDR token` to access the data plane:

`POST {{control_url}}/v3/assets`

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "@type": "https://w3id.org/edc/v0.0.1/ns/ContractRequest",
    "counterPartyAddress": "{{target-edc}}/control/api/v1/dsp",
    "protocol": "dataspace-protocol-http",
    "policy": {
        "@context": "http://www.w3.org/ns/odrl.jsonld",
        "@type": "odrl:Offer",
        "@id": "{{data-offer-id}}",
        "assigner": "{{target-bpn}}",
        "target": "{{asset-id}}",
        "odrl:permission": [
            {{permissions}}
        ],
        "odrl:prohibition": [],
        "odrl:obligation": []
    }
}
```
{% endcode %}

Extract the `@id` from the response, later to be used for `{{edr-id}}`.
This ID represents the EDR token and is required for the next steps.
Successfully reaching this stage confirms a successful negotiation.

#### Creating the Transfer Process

Use the EDR token ID to retrieve the `transferProcessId`:

`POST {{control_url}}/v2/edrs/request`

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "edc": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "@type": "QuerySpecDto",
    "offset": 0,
    "limit": 1,
    "filterExpression": [
        {
            "operandLeft": "contractNegotiationId",
            "operator": "=",
            "operandRight": "{{edr-id}}"
        }
    ]
}
```
{% endcode %}

Copy the `transferProcessId` from the response to proceed, later to be used for `{{transferProcessId}}`.

#### Retrieving the Data Address

To obtain the data address from which the dataset can be requested:

`GET {{MANAGEMENT-API}}/v2/edrs/{{transferProcessId}}/dataaddress`

The response contains two crucial data points:
- `endpoint` – The URL of the data plane providing the requested asset, later to be used for `{{endpoint}}`.
- `authorization` – The authorization information required for data retrieval, e.g. a token.

#### Requesting the Data

Finally, execute a `GET` request using the endpoint and authorization information.

Append the dataset ID as a path parameter:

`GET {{endpoint}}/{{dataset-id}}`
- Ensure that the **Authorization header** is set with the previously obtained authorization information (token).

The EDC uses the token to map the request to the appropriate data source.
The request will be forwarded to the provider’s `baseUrl` configured in the asset.
Any additional path parameters appended to `{{endpoint}}` will be included in the request to the data source.

#### Example Request Flow

If requesting dataset `1` from the provideras in the example at the top, the final request will be:

`GET https://example.com/dataset/1`
