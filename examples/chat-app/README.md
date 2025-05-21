# Example Use Case App - Chat App

## Introduction

With this example we will build and test the example Use Case Application "Chat App", demonstrating hands-on how to interact with an EDC when building a use case application.

This will take us through how two Use Case Applications communicate with each other using the "notification" pattern and set themselves up.

What separates this example from other projects is that it is fully integrated into our CI and will be evolved over time to demonstrate sovity's current endorsed way of building Use Case Applications at each point in time. The turorial itself is extracted by automation and thus ensured to be up-to-date and alive for all future versions.

> Note that this example does not use the Eclipse EDC Management API, but the sovity EDC API Wrapper which contains a well-typed API surface for the EDC with generated client libraries and contains endpoints not available for vanilla EDCs.

## Prerequisites

- Docker
  - Required for launching the Docker Compose environment the Chat App will be tested in
- JDK 21, including JAVA_HOME setup correctly
  - Required for building the Quarkus Backend with Gradle
- Node 22 + Yarn
  - Required for building the React Frontend with Yarn
- Bash / Git Bash
  - Required for running the scripts that allow you to easily build & start the Chat App
- GitHub Container Registry Login: `docker login ghcr.io`
  - Required for pulling sovity EDC CE Images
- GitHub Maven Registry Credentials
  - Required for pulling the sovity EDC CE API Client Library and Utilities
  - You need to provide a GitHub access token with the `packages:read` scope to access Maven repositories for dependencies.
    To add the token, create a file named `gradle.properties` in `HOME_DIRECTORY/.gradle/` with the following content:
    ```
    gpr.user=your-github-username
    gpr.key=your-github-access-token
    ```

## Interactive Tutorial

This example code base also has an [interactive / guided tutorial](tutorial/README.md) accompanying it.

## Running the finished example

```
# Bash / Git Bash
cd source-final
./start.sh
```

This should start a Docker Compose with two Chat Apps. Relevant URLs are:

- First Chat App + EDC: "Provider"
  - **Chat App UI**: http://localhost:13000
  - **Chat App Backend**: http://localhost:18080
  - Connector UI: http://localhost:11000
  - Connector Management API Access:
    - Management API URL: http://localhost:11000/api/management
    - Management API Key: `SomeOtherApiKey`
  - Connector Dataspace Identity:
    - Participant ID: `provider`
    - Connector Endpoint: http://provider/api/dsp
- Second Chat App + EDC: "Consumer"
  - **Chat App UI**: http://localhost:23000
  - **Chat App Backend**: http://localhost:28080
  - Connector UI: http://localhost:22000
  - Connector Management API Access:
    - Management API URL: http://localhost:22000/api/management
    - Management API Key: `SomeOtherApiKey`
  - Connector Dataspace Identity:
    - Participant ID: `consumer`
    - Connector Endpoint: http://consumer/api/dsp
