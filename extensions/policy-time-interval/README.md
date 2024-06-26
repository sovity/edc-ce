<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Time Interval Restricted Policy</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

This extension adds a policy function that validates the time of data consumption.

Adds permission function with left side expression `POLICY_EVALUATION_TIME` and supported
operators `EQ`, `NEQ`, `LT`, `LEQ`, `GT`, `GEQ`. The right side expression is expected to be in the following date
format `yyyy-MM-dd'T'HH:mm:ss.SSSXXX`.

## Why does this extension exist?

Limiting data offers to specific valid durations.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
