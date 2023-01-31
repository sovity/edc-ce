# EDC-Connector ClearingHouse Extension
To get started, a sample docker-compose file is located in the root folder.

## Configuration
Settings required for this extension:

- `EDC_CLEARINGHOUSE_LOG_URL`: https://clearing.dev.mobility-dataspace.eu/messages/log

## Logging

With this extension, the EDC-Connector can log to the ClearingHouse and gets a _201 Created_ with a _MessageProcessedNotificationMessage_ back in response.

Following event Use-Cases are logged to the ClearingHouse:
- Confimed ContractAgreements
- Completed Data-Transferprocesses

## License
This project is licensed under the Apache License 2.0.

## Contact
Sovity GmbH - contact@sovity.de