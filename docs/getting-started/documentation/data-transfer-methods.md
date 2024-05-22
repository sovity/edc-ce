Which data transfer methods are supported by the EDC-Connector?
========

The connector supports two different data transfer modes:

1. HTTPData: The provider EDC fetches the data from its own backend and pushes it to the consumer's desired data sink. The transfer flow is either initiated by EDC’s UI or via API from a data sink which could be a use case application (a1). The request for data transfer is handled by the control plane of the consumer and directed towards the control plane of the provider. With this call (a2) the access credentials of the desired data sink together with other necessary information like data sink URL are handed over to the provider. After successfully checking the context information (e.g. that the requestor is eligible to request data, i.e. a valid contract is existing), the control plane orchestrates a data plane to process the transfer request (a3). The data plane fetches the data from the defined data source (as defined in the asset creation step) with a REST call (a4) and caches the data of the response (a5), to finally transfer the data to the desired data sink (a6).
2. HTTPProxy: The provider EDC fetches the data and passes it on consumer's data transfer request synchronously back to the consumer.

The following diagram illustrates the different transmission modes:
![data-transfer-methods.png](images%2Fdata-transfer-methods.png)

# Consuming Data via HttpProxy / HTTP Pull
The Use-Case Backend-Application is involved in steps b1, b4, b5 and b8 of the diagram. It should provide an endpoint for receiving
the EDR (b4). These information can then be used to start the tranfser request (b5). The result of the transfer request
will contain the data (b8). Please see related [documentation](./pull-data-transfer.md) about how to implement it technically.
