<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Always True Policy</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension
This extension creates a Policy Definition `always-true` on EDC startup.

## Why does this extension exist?

While the default behavior for contract definitions with empty policies is not "default deny",
our UI will be ensuring non-empty access and contract policies.

Therefore, it is of interest to have an `always-true` policy to explicitly enable full access in contract definitions.

## License
Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact
sovity GmbH - contact@sovity.de
