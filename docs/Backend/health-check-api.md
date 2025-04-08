## Health Checks for Your Connectors

In this article, we describe the recommended APIs for performing health checks on your connectors from the outside of a cluster. These APIs ensure the connectivity and functionality of your services and connected core-services by returning status codes and relevant statistics.

### General API Accessibility

For a basic health check that should always return a 200 OK status and provide statistics about the connector, use the following API:

- **Endpoint**: `GET {{Management-API}}/wrapper/ui/pages/dashboard-page`
- **Description**: This API belongs to the API-Wrapper, which also provides connector statistics and details for the UI dashboard.

### Testing interaction with the Dataspace IAM

To test the interaction with the Dataspace IAM (MIW, DAPS, DIM, ...), it is recommended to use the catalog request of your own connector. The Dataspace IAM is integrated into this flow for the DSP message. The body of the request can have a limit set to 1 and should also return a 200 OK status.

- **Endpoint**: `POST {{Management-API}}/v3/catalog/request`
- **Request Body**: Please refer to the latest Postman collection for up-to-date details on the request body.

## Conclusion

By using these APIs, you can perform effective health checks and ensure the proper functionality and interaction of your connectors within the system. If you have further questions or need additional support, please refer to our documentation or contact our support team.
