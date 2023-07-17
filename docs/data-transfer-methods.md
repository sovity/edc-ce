Which data transfer methods are supported by the EDC-Connector?
========

The connector supports three different data transfer modes:

1. HTTPData: The consumer EDC fetches the data and pushes it to the data sink.
2. HTTPProxy: The consumer EDC fetches the data and passes it synchronously to the consumer.
3. OutOfBand: The data source transfers the data directly to the data sink, the data is not routed through the connector.

The following diagram illustrates the different transmission modes:

![data-transfer-methods.png](images%2Fdata-transfer-methods.png)
