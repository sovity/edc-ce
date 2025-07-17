---
icon: gear-complex-code
---

## What is the API-Wrapper?

### sovity API-Wrapper

The API Wrapper is a core component of the sovity EDC designed to simplify and standardize communication between your applications and the sovity EDC backend. It acts as an abstraction layer, enabling developers to interact with sovity EDC without dealing with complex backend logic.

### Key Features

- **Unified API Interface:** Provides a consistent set of endpoints for different data transfer types and integration scenarios
- **Extensibility:** Supports custom use cases by allowing you to build your own applications on top of the sovity EDC platform
- **Integration with HttpData-PULL:** Seamlessly manages data exchange flows using HttpData-PULL mechanisms and EDRs.

### How Does the API Wrapper Work?

The API Wrapper exposes RESTful APIs that your applications can call to initiate or respond to data transfers. Behind the scenes, it handles authentication, message formatting, and routing through the sovity EDC infrastructure.

### Use Case Application Blueprints

sovity provides Use Case Application blueprints, such as the **Chat-App**, which can be used and adapted as a starting point for your own use case application projects.

These blueprints demonstrate best practices for integrating with the sovity EDC API-Wrapper and serve as practical examples covering key concepts such as HttpData-PULL flows and EDRs.

We recommend referring to these blueprints early in your development to accelerate learning and ensure alignment with sovity standards.

You can explore the Chat-App source code here:  
[Chat-App on GitHub](https://github.com/sovity/edc-ce/tree/main/examples/chat-app)

### Getting Started

If you are a developer interested in integrating with sovity EDC:
1. Familiarize yourself with the API Wrapper concepts and endpoints
2. Review the Chat-App example for practical insights
3. Dive into the transfer flows like HttpData-PULL and EDR documentation for advanced flows

## Using the Java Client Library

This requires JDK17 or higher, and either a Gradle or Maven project.

### Installing The Java Client Library

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
