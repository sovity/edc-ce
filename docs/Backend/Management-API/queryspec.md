## QuerySpec in Management APIs

Some Management APIs include the ability to use a so-called QuerySpec to restrict the returns of the API. Certain settings can be made via parameters so that the result is limited and more specific.

### Supported Management APIs

The following Management-APIs support using the QuerySpec, for example:
- `/assets/request`
- `/policydefinitions/request`
- `/contractdefinitions/request`
- `/contractagreements/request`
- `/transferprocesses/request`
- `/catalog/request`

These APIs either have the QuerySpec as a complete body or the QuerySpec is part of the body. The structure looks like this:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@type": "https://w3id.org/edc/v0.0.1/ns/QuerySpec",
    "https://w3id.org/edc/v0.0.1/ns/offset": 0,
    "https://w3id.org/edc/v0.0.1/ns/limit": 100,
    "https://w3id.org/edc/v0.0.1/ns/sortOrder": "DESC",
    "https://w3id.org/edc/v0.0.1/ns/sortField": "fieldName",
    "https://w3id.org/edc/v0.0.1/ns/filterExpression": [
        {
            "https://w3id.org/edc/v0.0.1/ns/operandLeft": "assetId",
            "https://w3id.org/edc/v0.0.1/ns/operator": "=",
            "https://w3id.org/edc/v0.0.1/ns/operandRight": "test"
        }
    ]
}
```
{% endcode %}

For example, pagination can be enabled by changing the number of returned data records using the `limit` parameter and setting the starting point using the `offset` or more specific filtering can be done using the `filterExpression` array.

### Advanced Example Using the v3-API and a Custom Asset Property
`POST {{Management-API}}/v3/assets/request`

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@type": "https://w3id.org/edc/v0.0.1/ns/QuerySpec",
    "https://w3id.org/edc/v0.0.1/ns/offset": 0,
    "https://w3id.org/edc/v0.0.1/ns/limit": 100,
    "https://w3id.org/edc/v0.0.1/ns/filterExpression": [
        {
            "https://w3id.org/edc/v0.0.1/ns/operandLeft": "asset:prop:type",
            "https://w3id.org/edc/v0.0.1/ns/operator": "=",
            "https://w3id.org/edc/v0.0.1/ns/operandRight": "data.core.digitalTwinRegistry"
        }
    ]
}
```
{% endcode %}

The `POST` body should of course be adapted to the specific situation as needed.

### FAQ

<details>
<summary>Is there an implicit limit for returned records for query requests if no limit has been set in the QuerySpec?</summary>

Yes, if no limit has been specified in the QuerySpec, the connector outputs a maximum of 50 records. To exceed the limit, the limit must be added in the QuerySpec above this value. This can also be an extremely high value, such as 9999.
</details>

<details>
<summary>Is there anything I need to consider when setting the sortField?</summary>

 An existing field must be set as the value of sortField if used; otherwise, the EDC will respond with an error code (500 Internal Server Error).
</details>
