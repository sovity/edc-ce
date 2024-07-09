<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Flyway + Hikari Utils</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Module

Flyway and Hikari common code used between our EDC Launchers, tests and the Crawler.

It is un-opinionated regarding the location of the migration scripts and does not provide an EDC extension.

## Why does this extension exist?

Programmatically calling Flyways migrations is verbose, and unfortunately our EDC Flyway extension is riddled with legacy code.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
