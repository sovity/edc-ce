<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Clients:<br />Community Edition API Wrapper Implementation</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this component

EDC extension which provides API implementations for most of the Wrapper API endpoints. Excluded endpoints are either
implemented in other sovity Community Edition EDC Extensions or implemented in our Enterprise Edition.

## Why does this extension exist?

The goal is to design APIs so they can be used losslessly with OpenAPI 3.0 generators to provide different client 
libraries at ease, while extending or simplifying connector use and functionality. 

Furthermore, not using the often changing Eclipse EDC APIs, but rather our own facade 
allows better backwards compatibility for both our UIs and Use Case Applications.

With the move to JSON-LD both input and output of the Eclipse EDC APIs must be semantically interpreted. 
JSON-LD libraries are not available in all languages or well to use, therefore this API Wrapper can alleviate that pain,
providing simple and type-safe REST APIs,. materializing polymorphisms into sum types.

## License

Apache License 2.0 - see [LICENSE](../../../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
