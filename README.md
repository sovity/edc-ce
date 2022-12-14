# EDC-Connector Broker Extension

To get started, a sample docker-compose file is located in the resources/docs folder.

To be able to start an EDC-Connector with the broker-extensions, the AKI and SKI of the connector certificate must be entered as client-ID in the docker-compose and the .jks must be placed under the path specified in the docker-compose (in the example in the folder resources/vault/edc/, see edc_keystore setting).
