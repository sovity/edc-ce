<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Clients</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

To provide type-safe JSON APIs for both our Connector UI and Use Case Applications,
we provide an API Wrapper around the Connector over the JSON-LD Management API of the EDC Connector.

These APIs are more opinionated and use-case tailored.

This module contains:

- API Definitions:
    - [sovity Community Edition EDC API](./wrapper-api)
    - [sovity Enterprise Edition EDC API](./wrapper-ee-api)
    - [Broker / Connector Common API](./wrapper-common-api)
- API Client Libraries:
    - [Java API Client](./clients/java-client)
    - [TypeScript API Client](./clients/typescript-client)
    - more to come
- An [EDC Extension](./wrapper) serving the implementations for the sovity Community Edition API.
- [Broker / Connector Common Services](./wrapper-common-mappers)

## Why does this extension exist?

The goal is to design an API such that it can be used losslessly with OpenAPI 3.0 generators to provide different client
implementations. Furthermore, the decoupling of EDC Core APIs and a custom API facade allows better backwards
compatibility.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
