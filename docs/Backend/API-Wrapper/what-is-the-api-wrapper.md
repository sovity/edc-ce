---
icon: gear-complex-code
---

## What is the API-Wrapper?

### sovity API-Wrapper

The EDC API-Wrapper contains several APIs that are included with our Connector-as-a-Service (CaaS) and are made accessible via our type-safe generated client libraries.

#### CaaS API

Special API endpoints available in our Connector-as-a-Service (CaaS). For a list of APIs provided by the API-Wrapper and how to use them, please refer to our postman collection.

#### Use Case API

Generic API for Use Case Applications. Its goal is to replace the Management API, providing stable endpoints across milestones in our auto-generated client libraries. This API is still in development, so expect many new endpoints to be added in the near future.

#### UI API

API endpoints for our EDC UI: These endpoints might contain interesting data that a Use Case Application might benefit from, but please note that these endpoints are unstable and subject to change.

### Using the Java Client Library

This requires JDK11 or higher, and either a Gradle or Maven project.

#### Installing The Java Client Library

1. **Connect your Maven or Gradle Project to the Github Maven Registry:**
   - Maven: [Github Packages for Maven](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages)
   - Gradle: [Github Packages for Gradle](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-to-github-packages)

   This might require a Github Personal Access Token (PAT).

2. **Add our Java Client Library to your Maven/Gradle project:**
   - [Java Client Library](https://github.com/sovity/edc-extensions/packages/1825774)

### Using The Client

Feel free to use the endpoints of the aforementioned API groups:

{% code title="JAVA" overflow="wrap" lineNumbers="true" %}
```java
// Example usage of a use-case API endpoint:
KpiResult kpiResult = client.useCaseApi().getKpis();
```
{% endcode %}
