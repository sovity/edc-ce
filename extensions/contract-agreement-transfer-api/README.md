<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Contract Agreement Transfer API</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues">Request Feature</a>
  </p>
</div>

## About this Extension

This extension provides a single endpoint to initiate transfer processes via contract agreement ID.

## Why does this extension exist?

Our extended [EDC UI](https://github.com/sovity/EDC-UI) supports fetching contract offers from any catalog endpoint.

When initiating a transfer process with the default data management API, you need to provide the Connector
Endpoint (`asset:prop:originator`) of the providing connector.

The default data management API, however, __does not provide the Connector Endpoint__ of contract agreements, that is
required for initiating transfers in the default data management API.

When potentially connecting to multiple other connectors / catalogs, guessing the originator URL is not an
option. Re-fetching catalogs of all connectors is also not an option, since access policies and contract policies might
diverge, causing the asset of the contract agreement to not show up in the catalog anymore.

This forced us to create this extension to be able to comfortably initiate transfer processes with both a contract
agreement id and a data destination address.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
