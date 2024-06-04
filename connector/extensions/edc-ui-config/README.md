<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />EDC UI Extension Config</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

Our [EDC UI](https://github.com/sovity/edc-ui/) requires many configuration properties which exist in the EDC Backend.

This extension provides an endpoint on the Management Endpoint `/edc-ui-config` which allows our EDC UI to retrieve
additional `EDC_UI_` properties from the backend.

It will pass all config properties starting with `edc.ui.` in general.

## Why does this extension exist?

By not having to repeat ourselves when configuring the EDC UI, we save time and reduce the risk of errors.

## License

Apache License 2.0 - see [LICENSE](../../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
