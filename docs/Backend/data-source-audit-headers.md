# Additional Headers for Data-Source Requests

Requests towards data-sources include additional headers for the auditing of transfer requests by the provider’s backend service.
By that, the provider can manage consumer-specific access without the need to create separate assets for each consumer.

## Additional Headers

The following headers are included in the requests from the EDC's Data-Plane to the `http-datasource` specified in the providing asset:

1. **`edc-contract-agreement-id`**  
   - Contains the agreement-id for the specific contract between the provider and the consumer

2. **`edc-bpn`**  
   - Represents the Business Partner Number (BPN) of the consumer

## Key Objectives
- **Enhanced Auditability**: The backend service can log and monitor data requests made by consumers, ensuring traceability.
- **Simplified Configuration**: Providers avoid the need for creating separate assets per consumer.
- **HttpData-Push Transfers**: The same mechanism applies to HttpData-Push, making it straightforward.
