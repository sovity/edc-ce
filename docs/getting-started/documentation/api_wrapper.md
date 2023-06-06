Manage Your EDC Connector Via Our EDC API Wrapper Java Client Library
========

Introduction to the sovity EDC API Wrapper
========
The EDC API Wrapper contains several APIs that are shipped with our Connector-as-a-Service (CaaS) EDC Connectors and are made accessible via our type-safe generated client libraries. Please note that most of these APIs are not yet complete and are under development:
- **Use Case API**: Generic API for Use Case Applications. Its goal is to replace the Management API, so we can offer stable endpoints across milestones in our auto-generated client libraries. It's still in development, so expect many new endpoints to be added here in the near future.
- **UI API**: API endpoints for our EDC UI: These endpoints might contain interesting data, that a Use Case Application might profit from, but expect these endpoints to be unstable and subject to change.
- **Enterprise Edition API**: Special API endpoint only available in our Connector-as-a-Service (CaaS). Features such as File Storage are currently in development, but to be expected in the near future.

Using the Java Client Library
========
This requires JDK11 or higher, and either a Gradle or Maven project.

Installing The Java Client Library
========
Connect your Maven or Gradle Project to the Github Maven Registry
- Maven: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages
- Gradle: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-to-github-packages
- This might require a Github Personal Access Token (PAT)
Add our Java Client Library to your Maven/Gradle project: https://github.com/sovity/edc-extensions/packages/1825774

Configuring The Client
========
- Configure the Client with either an API Key or OAuth2 Client Credentials: https://github.com/sovity/edc-extensions/tree/main/extensions/wrapper/client#usage
- Your management API URL should look like https://your-connector-name.prod-sovity.azure.sovity.io/control/data

Using The Client
========
Feel free to use the endpoints of the aforementioned API groups.
Example Usage of a Use Case API Endpoint:
```java
KpiResult kpiResult = client.useCaseApi().kpiEndpoint();
```
