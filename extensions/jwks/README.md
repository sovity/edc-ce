<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />JWKS</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

The JWKS-Extension provides an endpoint in the default API of the EDC-Connector, that returns the
[JWKS]{https://datatracker.ietf.org/doc/html/rfc7517#section-5} of the connector. It can be accessed
using the `:{WEB_HTTP_PORT}/{WEB_HTTP_PATH}/jwks` (default: `:11001/api/jwks`) endpoint.

## Why does this extension exist?

The JWKS-endpoint can be used to validate tokens issued by the EDC-Connector. This is part of our
goal of simplifying the on-boarding process of connectors to a DAPS.

## Configuration

### X509 Secret Alias

The alias of the pem-encoded X509-certificate stored in the `Vault` is determined by
the `edc.transfer.proxy.token.verifier.publickey.alias` property.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
