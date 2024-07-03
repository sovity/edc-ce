<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Catalog Crawler</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

Catalog crawler for the Authority Portal.

Authority Portal catalog crawlers are deployed as separate deployment units, one for each environment / daps.

## Why does this component exist?

The Authority Portal uses a non-EDC stack, and the EDC stack cannot handle multiple data spaces at once.

To still be able to display the data catalogs in the authority portal, these separately deployed catalog
crawlers will connect to the existing Authority Portal database and crawl data offers for their configured
environment.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
