<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />IDS Clearing House Client Extensions</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues">Request Feature</a>
  </p>
</div>

## About this Extension

This extension communicates contract agreements and data transfers to an IDS Clearing House.

On successful contract agreements / data transfers it will send an IDS LogMessages and expect a 201 Created response.

## Why does this extension exist?

This extension was developed in cooperation with the Mobility Data Space (MDS).

## Getting Started

Check out the [Getting Started](https://github.com/sovity/edc-extensions/tree/main/connector#getting-started) section of
our EDC Community Edition.

Our EDC Community Edition is built with both the clearing house and broker extensions and is ready to be used in the
Mobility Data Space (MDS).

## Configuration

The Clearing House URL can be configured with the ENV Var:

```dotenv
EDC_CLEARINGHOUSE_LOG_URL=https://clearing.test.mobility-dataspace.eu/messages/log
```

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
