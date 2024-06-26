<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Referring Connector Restricted Policy</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

This extension adds a policy function that allows validating the consuming EDC Connector's DAT claim
value `referringConnector`. This allows to limit data-offer access to specific consumers.

Adds permission function with left side expression `REFERRING_CONNECTOR` with the currently supported
`EQ` operator.

## Why does this extension exist?

Especially in data spaces where data is shared with business partners for specific purposes, data assets need to
be offered to desired consumers only.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
