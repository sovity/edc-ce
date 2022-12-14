# EDC-Connector Broker Extension

To get started, a sample docker-compose file is located in the resources/docs folder.

To be able to start an EDC-Connector with the broker-extensions, the AKI and SKI of the connector certificate must be entered as client-ID in the docker-compose and the .jks must be placed under the path specified in the docker-compose (in the example in the folder resources/vault/edc/, see edc_keystore setting).

Example of a client-ID entry:
EDC_OAUTH_CLIENT_ID: 7X:7Y:FZ:1A:EB:FC:7D:3E:8F:1C:96:61:E2:81:F0:BE:C4:55:B2:94:keyid:6A:2B:3C:5D:AE:7F:BG:FH:9I:95:DA:AF:8C:42:9F:C8:00:00:28:80
