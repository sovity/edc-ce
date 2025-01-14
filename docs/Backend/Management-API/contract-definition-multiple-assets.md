## Contract Definition: Multiple Assets

This documentation provides instructions on how to use the `IN` operator in the `assetsSelector` to specify multiple assets in a single `ContractDefinition`.

### Example Configuration

Below is an example of a `ContractDefinition` configuration where the `assetsSelector` is used to specify multiple assets that should be governed by the same `ContractDefinition`. The `IN` operator is used to list these assets.

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
   "@id": "contractdefinition",
   "@type": "https://w3id.org/edc/v0.0.1/ns/ContractDefinition",
   "https://w3id.org/edc/v0.0.1/ns/accessPolicyId": "always-true",
   "https://w3id.org/edc/v0.0.1/ns/contractPolicyId": "always-true",
   "https://w3id.org/edc/v0.0.1/ns/assetsSelector": [
    {
      "@type": "CriterionDto",
      "https://w3id.org/edc/v0.0.1/ns/operandLeft": "https://w3id.org/edc/v0.0.1/ns/id",
      "https://w3id.org/edc/v0.0.1/ns/operator": "in",
      "https://w3id.org/edc/v0.0.1/ns/operandRight": ["test1","test2"]
    }
   ]
}
```
{% endcode %}


### Important Considerations

When defining multiple assets in the `assetsSelector` for a `ContractDefinition`, it is crucial to use the `IN` operator rather than specifying multiple arrays for multiple assets. Although the API will accept multiple arrays in the `assetsSelector`, this approach can lead to issues with catalog requests from potential consumers. Specifically, the catalog request response will fail to resolve all assets beyond the first. 

To ensure proper functionality and compatibility, always use the `IN` operator to add multiple assets within an `assetsSelector`. This method guarantees that all specified assets are correctly processed and available in catalog requests.
