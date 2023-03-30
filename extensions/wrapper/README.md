<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Client</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

This extension contains multiple APIs designed for auto-generation for API Clients.

[This extension](./wrapper) contains multiple components:

- Use Case API
- EDC UI API
- Further APIs to be included for our API Clients
- Our auto generated [Java API Client](./client)
- Our auto generated [TypeScript API Client](./client-ts)

For faster development we created this extension which will bundle multiple APIs our extended EDC supports.

The goal is to design the API in a way it can be used losslessly with OpenAPI 3.0 generators so we can seamlessly
generate both Java Clients and TypeScript Clients.

## Why does this extension exist?

Our UI needed an API we control. There were also concerns about the functionality of the Data Management API.

As there was a need to generate API clients for both Use Case Applications and TypeScript clients we decided to combine
the tasks to save time.

By having a dedicated API designed for auto generation, targeted for our consumers, we will be able to share code,
iterate faster and deliver faster.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
