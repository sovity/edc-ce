## Editing Existing Asset

You can change existing Assets, e.g., if you want to change the `clientSecretKey` or the URL of your Data Source.

### 1. Fetch Your Asset by Asset ID

Send a GET request to `{{EDC_MANAGEMENT_URL}}/v2/assets/{{ASSET_ID}}`

This will provide you with a response similar to this:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@id": "urn:uuid:2e95a999-ea92-42bf-a3ad-bb04b614eb77-urn:uuid:486826eb-c626-414a-98c4-c3539555668d",
    "@type": "edc:Asset",
    "edc:properties": {
        "edc:modified": "06/12/2023 14:07:50",
        //...
        "edc:contenttype": "application/json"
    },
    "edc:dataAddress": {
        "@type": "edc:DataAddress",
        "edc:proxyPath": "true",
        "oauth2:tokenUrl": "https://keycloak.io/realms/Portal/protocol/openid-connect/token",
        "oauth2:clientId": "Client-001",
        "edc:type": "HttpData",
        "edc:proxyMethod": "true",
        "oauth2:clientSecretKey": "client-secret-001",
        "edc:name": "Backend Data Service - App",
        "edc:proxyQueryParams": "true",
        "edc:proxyBody": "true",
        "edc:contentType": "application/json",
        "edc:baseUrl": "https://datasource.io/api/serialpart/public/urn:uuid:b755983b-4d5a-47ff-a335-3c0f59697036"
    },
    "@context": {
        "dct": "https://purl.org/dc/terms/",
        "tx": "https://w3id.org/tractusx/v0.0.1/ns/",
        "edc": "https://w3id.org/edc/v0.0.1/ns/",
        "dcat": "https://www.w3.org/ns/dcat/",
        "odrl": "http://www.w3.org/ns/odrl/2/",
        "dspace": "https://w3id.org/dspace/v0.8/"
    }
}
```
{% endcode %}

Copy the content, including brackets, between `"edc:dataAddress"` and `"@context"`.

### 2. Update Data Address

Send a PUT request to this Endpoint: `{{EDC_MANAGEMENT_URL}}/v2/assets/{{ASSET_ID}}/dataaddress`

Make sure to add the content you copied before after `"@type": "edc:DataAddress"`.

Additionally, add:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
"@context": {
    "edc": "https://w3id.org/edc/v0.0.1/ns/"
}
```
{% endcode %}

## Example

For example, to change the `clientSecretKey`, use the Endpoint and a Body similar to this:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "edc": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "edc:dataAddress": {
        "@type": "edc:DataAddress",
        "edc:proxyPath": "true",
        "oauth2:tokenUrl": "https://keycloak.io/realms/Portal/protocol/openid-connect/token",
        "oauth2:clientId": "Client-001",
        "edc:type": "HttpData",
        "edc:proxyMethod": "true",
        "oauth2:clientSecretKey": "{{NEW CLIENT SECRET ALIAS}}",
        "edc:name": "Backend Data Service - App",
        "edc:proxyQueryParams": "true",
        "edc:proxyBody": "true",
        "edc:contentType": "application/json",
        "edc:baseUrl": "https://datasource.io/api/serialpart/public/urn:uuid:b755983b-4d5a-47ff-a335-3c0f59697036"
    }
}
```
{% endcode %}

Please make sure to adjust the relevant parameters in your case (e.g., `clientSecretKey`).
