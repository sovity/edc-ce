## Data-Transfer using HttpData-Pull and EDR

{% hint style="warning" %} In version 11.0.0, the in the following documentation required EDR-APIs are only available in the Catena configuration and not when using an IAM-mock or a DAPS. {% endhint %}

**Parameterization**: In certain scenarios, it is beneficial to expose multiple datasets via a single asset, reducing contract negotiations and catalog size, thus improving Connector scalability.

{% hint style="info" %} Parameterization is optional and does not have to be activated by the Provider if the datasource API does not require or enable it. In this case, the corresponding parameters do not have to be enabled when creating an asset and the Consumer does not have to add any additional parameters to the endpoint from the EDR. {% endhint %}

#### Provider: Asset

A datasource API can be structured with a base URL and path parameters to differentiate datasets:
- `https://example.com/dataset/1`
- `https://example.com/dataset/2`
- `https://example.com/dataset/3`
- `https://example.com/dataset/4`

In this case, the base URL remains: `https://example.com/dataset/`.

##### Defining the Asset

To add an asset that references such a base URL which supports parameterization, use the following API request:

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
- `"proxyPath": "true"`: enables path-based dataset retrieval for the Consumer (Parameterization)
- `"proxyQueryParams": "true"`: allows dynamic query parameter inclusion for the Consumer (Parameterization)

{% hint style="info" %} As parameterization is optional, these settings do not need to be set to "true" if the datasource API doesn't support it, then either don't add them at all or set them to "false". {% endhint %}

After adding the asset, the asset only needs to be linked in a Contract Definition/Data Offer along with Policies in order to make it available for potential Consumers to consume.

#### Consumer: Consuming Data

##### Step 1: Catalog Querying

To access the dataset, query the Provider's EDC catalog and identify the required asset. Extract the following details:
- `dcat:dataset.{asset}.odrl:hasPolicy.@id` - The data offer ID, later to be used for `{{data-offer-id}}`
- `dcat:dataset.{asset}.odrl:hasPolicy.odrl:permission` - The policies, in this case permissions, needed to start the negotiation, later used for `{{permissions}}`

##### Step 2: Negotiating the EDR

Next, request the `EDR token`:

`POST {{MANAGEMENT-API}}/v2/edrs`

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

##### Step 3: Transfer Process

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

##### Step 4: Data Address

To obtain the data address from which the dataset can be requested:

`GET {{MANAGEMENT-API}}/v2/edrs/{{transferProcessId}}/dataaddress`

The response contains two crucial data points:
- `endpoint` – The URL of the data plane providing the requested asset, later to be used for `{{endpoint}}`.
- `authorization` – The authorization information required for data retrieval, e.g. a token.

##### Step 5: Requesting the Data

Finally, execute a `GET` request using the endpoint and authorization information.

Append the dataset ID as a path parameter:

`GET {{endpoint}}/{{dataset-id}}`
- Ensure that the **Authorization header** is set with the previously obtained authorization information (token).

The EDC uses the token to map the request to the appropriate data source.
The request will be forwarded to the Provider’s `baseUrl` configured in the asset.
Any additional path parameters appended to `{{endpoint}}` will be included in the request to the data source.

{% hint style="info" %} As parameterization is optional for the Provider, the additional parameters like {{dataset-id}} in this example do not need to be set by the Consumer if the Provider doesn't support it. {% endhint %}

##### Parameterized Example

As the information is obtained from the contents of the EDR, if requesting dataset `1` from the Provider as in the example at the top, the final request would be:

`GET https://example.com/dataset/1`
-  plus additional authorization headers as specified in the EDR
