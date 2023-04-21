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

This extension contains multiple APIs designed for auto-generation of API Clients to speed up development process. It
contains multiple components:

- APIs for
    - EDC UI
    - Use Cases
    - Further APIs
- An [EDC Extension](./wrapper) serving the implementations
- Auto generated
    - [Java API Client](./client)
    - [TypeScript API Client](./client-ts)
    - more to come

## Why does this extension exist?

The goal is to design an API such that it can be used losslessly with OpenAPI 3.0 generators to provide different client
implementations. Furthermore, the decoupling of EDC Core APIs and a custom API facade allows better backwards
compatibility.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
