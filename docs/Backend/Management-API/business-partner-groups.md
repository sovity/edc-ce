## Business Partner Group policy
 
 This documentation provides an overview of how to use the Management-API to manage and evaluate Business Partner Group policies.
 The Business Partner Group policy enables categorizing business partners into groups and defining access- and contract-policies based on their membership.
 For example, a company can restrict access to certain assets to members of specific groups like "group1" or "group2".
 This is useful for scenarios where assets or resources are only accessible to specific groups of business partners.
 
 ## API Endpoints and Usage
 
 ### Assigning a BPN to Groups
 
 This endpoint assigns a specific BPN to one or more groups. The groups do not need to be created explicitly; they are defined dynamically upon assignment.
 
 **Request**  
 `POST {{Management-API}}/business-partner-groups`
 
 **Payload Example**:
 {% code title="JSON" overflow="wrap" lineNumbers="true" %}
 ```json
 {
   "@context": {
     "tx": "https://w3id.org/tractusx/v0.0.1/ns/"
   },
   "@id": "BPNL000000000000",
   "tx:groups": [
     "group1",
     "group2"
   ]
 }
 ```
 {% endcode %}
 
 ### Checking Group Membership of a BPN
 
 This endpoint retrieves the groups a specific BPN is assigned to. Use this to verify that the assignment was successful.
 
 **Request**  
 `GET {{Management-API}}/business-partner-groups/BPNL000000000000`
 
 **Response Example:**
 {% code title="JSON" overflow="wrap" lineNumbers="true" %}
 ```json
 {
   "@id": "BPNL000000000000",
   "tx:groups": [
     "group1",
     "group2"
   ]
 }
 ```
 {% endcode %}
 
 ### Creating a Business Partner Group Policy
 This endpoint defines a policy to evaluate whether a BPN belongs to one or more groups.
 The policy can later be used in a Contract Definition/Data Offer as an access- or contract-policy.
 The example below uses the `isAnyOf` operator to check if a BPN is part of "group1" or "group2".
 
 **Request**
 `POST {{Management-API}}/v3/policydefinitions`
 
 **Payload Example**:
 {% code title="JSON" overflow="wrap" lineNumbers="true" %}
 ```json
 {
     "@type": "https://w3id.org/edc/v0.0.1/ns/PolicyDefinitionDto",
     "@id": "bpn-group1-and-group2",
     "https://w3id.org/edc/v0.0.1/ns/policy": {
         "@context": "http://www.w3.org/ns/odrl.jsonld",
         "@type": "http://www.w3.org/ns/odrl/2/Set",
         "permission": [
             {
                 "action": "USE",
                 "http://www.w3.org/ns/odrl/2/constraint": [
                     {
                         "leftOperand": "https://w3id.org/tractusx/v0.0.1/ns/BusinessPartnerGroup",
                         "operator": "http://www.w3.org/ns/odrl/2/isAnyOf",
                         "rightOperand": "group1,group2"
                     }
                 ]
             }
         ]
     }
 }
 ```
 {% endcode %}
 
 The following ODRL operators are supported:
 - **eq**: Equal to a value
 - **neq**: Not equal to a value
 - **isPartOf**: Checks if a value is part of a group
 - **isAllOf**: Checks if all values match
 - **isAnyOf**: Checks if any value matches
 - **isNoneOf**: Ensures no value matches
