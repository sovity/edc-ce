Which data transfer methods are supported by the EDC-Connector?
========

The connector supports three different data transfer modes:

1. HTTPData: The provider EDC fetches the data from its own backend and pushes it to the consumer's desired data sink.
2. HTTPProxy: The provider EDC fetches the data and passes it on consumer's data transfer request synchronously back to the consumer.

The following diagram illustrates the different transmission modes:


![data-transfers](https://github.com/sovity/edc-extensions/assets/75306992/2b0872d5-aa0d-4be3-822d-c2b6fe400624)
