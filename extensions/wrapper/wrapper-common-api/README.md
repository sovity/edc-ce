<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Clients:<br />Common API Models</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this module

Common API models between the sovity Community Edition EDC API, sovity Enterprise Edition EDC API and/or the Broker
Server API.

## Why does this module exist?

APIs to be implemented outside the wrapper extension itself create their own Gradle Modules,
e.g. [:extensions:wrapper:wrapper-ee-api](../wrapper-ee-api).

There are few models we can profit from sharing between all APIs. For example,
[`PolicyDto`](src/main/java/de/sovity/edc/ext/wrapper/api/common/model/PolicyDto.java), which
contains a supported subset of the original EDC Policy-Entity. We create such a custom policy model
because the core EDC Policy model struggles in OpenAPI Specification YAMLs due to its polymorphism.

## License

Apache License 2.0 - see [LICENSE](../../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
