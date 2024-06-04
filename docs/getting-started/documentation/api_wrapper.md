Manging a sovity EDC Connector via the API Wrapper Java Client Library
========

Introduction to the sovity EDC API Wrapper
========
The sovity EDC API Wrapper contains several APIs, of which some are available in either our sovity EDC CE or our sovity
CE EE / Connector-as-a-Servcie (CaaS). These APIs are made accessible via type-safe generated client libraries. Please
note that most of these APIs are not yet complete and are under development:

- **Use Case API**: Generic API for Use Case Applications. Its goal is to replace the Management API, so there can be
  stable endpoints across milestones in the auto-generated client libraries. It's still in development, so expect many
  new endpoints to be added here in the near future.
- **UI API**: API endpoints for the sovity EDC UI: These endpoints might contain interesting data, that a Use Case
  Application might profit from, but expect these endpoints to be unstable and subject to change.
- **Enterprise Edition API**: Special API endpoint only available in the Connector-as-a-Service (CaaS). Features such as
  File Storage are currently in development, but to be expected in the near future.

Using the Java Client Library
========
This requires JDK11 or higher, and either a Gradle or Maven project.

Installing The Java Client Library
========
Connect your Maven or Gradle Project to the Github Maven Registry

-
Maven: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages
-
Gradle: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-to-github-packages
- This might require a Github Personal Access Token (PAT)
  Add the Java Client Library to your Maven/Gradle project: https://github.com/sovity/edc-extensions/packages/1825774

Configuring The Client
========

- Configure the Client with either an API Key or OAuth2 Client
  Credentials: https://github.com/sovity/edc-extensions/tree/main/connector/extensions/wrapper/clients/java-client#usage
- Your management API URL should look like https://your-connector-name.prod-sovity.azure.sovity.io/control/data

Using The Client
========
Feel free to use the endpoints of the aforementioned API groups.

A full example providing and consuming a data offer using the API Wrapper Client Library can be found
in [ApiWrapperDemoTest.java](../../../connector/tests/src/test/java/de/sovity/edc/e2e/ApiWrapperDemoTest.java).
