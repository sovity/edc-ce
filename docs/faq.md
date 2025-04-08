---
icon: message-question
---

## Welcome to our Frequently Asked Questions (FAQ) collection!

This section is designed to provide quick and clear answers to the most common queries we receive. Whether you're looking for information on our products, services, policies, or need help with troubleshooting, you'll find the answers here. If you can't find the answer to your question, don't hesitate to open a [dicussion](https://github.com/sovity/edc-ce/discussions).

### How does the EDC API-key work for backend and frontend for API-authentication?

**Backend (EDC):**
The variable ```EDC_API_AUTH_KEY``` in the EDC backend is used to define the API-key for the Management-API and API-Wrapper in the first place. External requests from external services to the EDC backend and its Management-API/API-Wrapper are then authenticated against this value and the requests must contain this API-key accordingly.
 
**Frontend (EDC UI):**
The frontend is such an external service whose API requests to the Management-API/API-Wrapper of the EDC backend must be authenticated like any other API request, hence the variable ```EDC_UI_MANAGEMENT_API_KEY``` in the EDC UI. It therefore tells the UI which API-key it should use for its own requests to the EDC backend in order to be successfully authenticated there, so that the UI can query and display data from the EDC backend or create assets etc.

**General Note:** When using docker-compose quotation marks shouldn't be used and therefore the API-Key can't start with a special character like ```&```.

### Can the EDC forward the response content of an error message from the data sink/source?

It is not possible with the EDC, as this is an intended behavior. The EDC will only return the response-content if the HTTP status code is in the [200..300) range. Otherwise, the response-content will be ignored.

### What authorization mechanisms are available in the EDC?

There are three levels of authorization in the EDC:
1. **Protected Management-API / API-Wrapper**: OAuth2
2. **EDC to EDC communication**: normally DAPS/MIW 
3. **Protected provider backends**: OAuth2, Basic Auth, API-Keys

### How do I determine the correct OfferId to use when starting a contract negotiation via the Management-API?

The offer-id consists of the provider's contract-definition-id, the provider's asset-id, and an additional UUID provided by the provider. These are then base64 encoded, appended to each other separated by a colon, and referred to as the offer-id. The response to the catalog request contains the hasPolicy-id, which should be used as the offer-id according to the schema:
-> base64(contractDefinitionId):base64(assetId):base64(serverProvidedUuid)

Unfortunately, when starting the contract negotiation, the policy of the provider's offer must be copied. All necessary information should be included in the provider's response to the catalog request, as it provides the provider's policies attached to his offerings.

### Is there a default limit on the number of records returned for query requests if no limit is set in the QuerySpec?

Yes, if no limit has been specified in the QuerySpec, the connector outputs a maximum of 50 records. To exceed this limit, you must specify the desired limit in the QuerySpec, which can be an extremely high value, such as 9999.

### What are the conditions for deleting Assets, Policies, and ContractDefinitions in the EDC?

Assets, Policies, and ContractDefinitions can be deleted using the details dialog of the corresponding entity in the UI or via the APIs. However, there are certain conditions that must be met; otherwise, the delete operation will fail.

**Delete Conditions for Assets**
Assets cannot be deleted once a contract has already been negotiated with another connector. If this condition is not met, the delete operation will fail.

**Delete Conditions for Policies**
Policies cannot be deleted if they are referenced in a ContractDefinition.

**Delete Conditions for ContractDefinitions**
ContractDefinitions can always be deleted. As a result, the offering will no longer appear in the catalog of other connectors. Additionally, other connectors will no longer be able to transfer data based on contracts that have been negotiated using this ContractDefinition.

### Is it possible to delete an existing contract?

No, this is neither possible via the frontend nor via the backend APIs. Contracts can only be viewed but not edited or deleted.

### Can a user publish third-party data assets, such as REST API endpoints?

Yes, a user can publish data assets, including REST API endpoints from third parties. For example, you can use sources like https://www.google.com as a data source, which would transfer the HTML-source code of google.com. However, it is crucial to ensure the protection of your own APIs for this reason, so that no one can pass it off as their own. We recommend securing your APIs using methods such as API-keys or OAuth2 to control access and protect your data.  

### Can a Contract Negotiation Fail?

**1. The Consumer Does Not Comply with the Provider’s Contract Policy**

During the creation of the contract definition, the provider has the opportunity to set conditions (policies) for its data offering.

- The **access policy** describes under which conditions a data offer becomes visible to another connector when the provider's catalog is requested. If a potential consumer sees the data offer when the provider's catalog is requested, the consumer has the opportunity to start a contract negotiation.

- The **contract policy** describes the conditions under which a potential consumer can conclude a contract when starting a contract negotiation. If the potential consumer satisfies the access policy but the conditions of the contract policy are not met, the consumer can see the offer but cannot conclude a successful contract negotiation.

**2. Terminating an Ongoing Contract Negotiation via API Call**

Within the Management-API, there is an API that can abort and terminate ongoing contract negotiations at any time, independent of policies: `POST /v2/contractnegotiations/{id}/terminate`. If this API is used while a contract negotiation is ongoing, the contract negotiation will be aborted prematurely, ending in the terminated state.

### Why am I getting a 500 error when accessing the /public endpoint of the Data-Plane?

This issue can be caused by the connected customer-backend. The core-edc may convert error codes, leading to this issue. For example, a customer-backend returning a 400 code could be translated to a 500 by the core-edc.

### Do productive Connectors need to be public reachable?

Yes, Connectors need to have public-facing endpoints when deployed for productive usage in a dataspace, specifically the Dataspace Protocol (DSP) endpoint, to enable communication between Connectors. This is necessary because during processes such as contract negotiation, the Connectors must be able to exchange DSP-messages to perform the negotiations. Having a public DSP-endpoint allows these interactions to occur in a productive deployment environment. Contract negotiations and other communications happen between the connectors in a peer-to-peer manner.

### Do data sinks need to be public reachable in productive environments?

Yes, the architecture relies on the data provider directly connecting to the data sink of the consumer, which is designed for scaling and technical efficiency. This setup reduces the need for data to pass through the consuming EDC, optimizing bandwidth, reducing latency and avoiding technical bottlenecks, this is thus a technical design decision. To secure the data sink, measures such as IP-whitelisting of data-providers or API-keys for the data sink should be used to ensure that only authorized parties can access and write to the data sink.

### What happens during policy evaluation, if a not-supported constraint is used in a policy?

If the EDC encounters a constraint it does not recognize within a policy, the EDC is designed to simply ignore that constraint during the evaluation process of the access- or contract-policy. This means that the unrecognized constraint will not impact the outcome of the evaluation.

### How can I identify and resolve issues caused by invalid policies?

When invalid policies exist, typical symptoms include the inability to fetch all policies via the Management-API, and if the policy is part of a Contract-Offer, the Catalog-Request may fail for a Consumer EDC.

To identify and resolve these issues, follow these steps:

1. Use the UI to list all created policies: The UI can display all policies created in the Connector. It utilizes the API-Wrapper, which operates differently than the Management-API/Catalog Request and can serve as a first reference point.

2. Iterate and verify individual policies: For each policy, perform a GET request via the Management-API to identify the specific policies causing the issue, they will return an error at the call execution instead of policy details. This method helps pinpoint the problematic policies.

A typical issue stems from missing contexts during the policy creation process. Ensure that all necessary context is provided to minimize error possibilities or delete the policy if not needed. If a contract offer exists with the policy that has been deleted, this must also be deleted.

### Is it possible to exchange data between different organizations in a data space?

If two connectors, regardless of their organization, are in the same data space, they can request the catalog from each other and then see the data offers that they are allowed to see according to the access policy and also negotiate successfully if they meet the contract policy.

### Does the EDC pass on error codes received from other systems through its API calls unchanged, or does it modify them?

The EDC does not always pass through error codes as they are received from other systems. Instead, it is known to modify certain error codes before returning them, which can make diagnosing the original issue more challenging.

For example: When the EDC receives an error code like 403 Forbidden from another system, it might map and return this as a 400 Bad Request instead. This behavior can obscure the original issue, as the returned error code does not reflect the root cause accurately. This mapping behavior can make debugging and understanding the actual problem more complex. As a result, users need to be aware of this behavior when troubleshooting issues with the EDC.
