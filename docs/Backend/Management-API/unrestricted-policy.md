## Unrestricted Policy
 
 Everyone will be able to access the data offer using this policy as access-policy or will be allowed to conclude a contract negotiation when using it as contract-policy.
 
 `POST {{Management-API}}/v3/policydefinitions`
 
 {% code title="JSON" overflow="wrap" lineNumbers="true" %}
 ```json
 {
    "@context": {
        "edc": "https://w3id.org/edc/v0.0.1/ns/",
        "odrl": "http://www.w3.org/ns/odrl/2/"
    },
    "@type": "PolicyDefinitionDto",
    "@id": "unrestricted",
    "edc:policy": {
        "odrl:permission": [
            {
                "odrl:action": {
                    "odrl:type": "USE"
                }
            }
        ]
    }
 }
 ```
 {% endcode %}
