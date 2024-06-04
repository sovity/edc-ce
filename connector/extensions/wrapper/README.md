<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">sovity EDC API Wrapper</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## sovity EDC API Wrapper

We provide a full type-safe and opinionated API Wrapper for better access to the EDC Connector's functionality.

## Explore

Create and consume Data Offers using clean type-safe JSON REST APIs:
- [API Wrapper OpenAPI YAML](../../docs/api/sovity-edc-api-wrapper.yaml).
- [Java API Client Library](./clients/java-client)
- [TypeScript API Client Library](./clients/typescript-client)

## Compatibility

Our EDC API Wrapper APIs and API Clients are compatible with both our sovity EDC Community Edition and sovity EDC Enterprise Editions.

## Modules

- The [sovity EDC API Wrapper Extension](./wrapper), serving implementations for our Community Edition APIs.
- API Definitions:
  - The sovity Community Edition EDC [API Definitions](./wrapper-api), including the [Connector UI API](wrapper-api/src/main/java/de/sovity/edc/ext/wrapper/api/ui) and [Use Case API](wrapper-api/src/main/java/de/sovity/edc/ext/wrapper/api/usecase).
  - The sovity Enterprise Edition EDC [API Definitions](./wrapper-ee-api).
- [Client Libraries](./clients) and example projects:
  - [Java API Client Library](./clients/java-client)
  - [Java API Client Library Example](./clients/java-client-example)
  - [TypeScript API Client Library](./clients/typescript-client)
  - [TypeScript API Client Library Example](./clients/typescript-client-example)
- Utilities:
  - Broker UI / Connector UI [Common Models](./wrapper-common-api)
  - Broker / Connector [Common Services](./wrapper-common-mappers)

## License

Apache License 2.0 - see [LICENSE](../../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
