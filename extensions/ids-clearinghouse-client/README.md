<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />IDS Clearing House Client Extension</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

This extension communicates contract agreements and data transfers to an IDS Clearing House.

On successful contract agreements / data transfers, the extension will send IDS LogMessages and expect a `201 Created`
HTTP response.

## Why does this extension exist?

Notary logging is a crucial part of dataspaces to ensure compliance and non-repudiation of data transfers.

## Getting Started

Our MDS Community Edition EDC is built with both the Clearing House and Broker extensions and is ready to
be used in the Mobility Data Space (MDS).

## Configuration

The Clearing House URL can be configured with the ENV var:

```dotenv
EDC_CLEARINGHOUSE_LOG_URL=https://clearing.test.mobility-dataspace.eu/messages/log
```

To disable the extension (per default enabled) you can use following ENV var:

```dotenv
CLEARINGHOUSE_CLIENT_EXTENSION_ENABLED=false
```

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
