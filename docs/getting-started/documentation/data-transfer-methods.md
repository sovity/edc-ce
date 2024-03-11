Which data transfer methods are supported by the EDC-Connector?
========

The connector supports three different data transfer modes:

1. HTTPData: The provider EDC fetches the data from its own backend and pushes it to the consumer's desired data sink.
2. HTTPProxy: The provider EDC fetches the data and passes it on consumer's data transfer request synchronously back to the consumer.

The following diagram illustrates the different transmission modes:
![data-transfer-methods.png](images%2Fdata-transfer-methods.png)
