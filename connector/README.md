<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-broker-server-extension">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">Broker Server:<br />Docker Images</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-broker-server-extension/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-broker-server-extension/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## Broker Server Image

The Broker Server Extension together with other EDC Extensions are built into Docker Images.

## Different Image Types

Our EDC Community Edition builds several docker images in different configurations.

| Docker Image                                                                                                | Type              | Purpose                                                                                          | Features                                                                                                                                                      |
|-------------------------------------------------------------------------------------------------------------|-------------------|--------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [broker-server-dev](https://github.com/sovity/edc-broker-server-extension/pkgs/container/broker-server-dev) | Development       | <ul><li>Lightweight local development</li><li>Used in EDC UI's Getting Started section</li></ul> | <ul><li>IDS Broker Server Extension(s)</li><li>Management API Auth via API Keys</li><li>Mock IAM</li></ul>                                                    |
| [broker-server-ce](https://github.com/sovity/edc-broker-server-extension/pkgs/container/broker-server-ce)   | Community Edition | <ul><li>Deploy the Broker Server</li></ul>                                                       | <ul><li>IDS Broker Server Extension(s)</li><li>Management API Auth via API Keys</li><li>DAPS Authentication</li><li>PostgreSQL Persistence & Flyway</li></ul> |

## Image Tags

| Tag     | Description                       |
|---------|-----------------------------------|
| latest  | latest version of our main branch |
| release | latest release of this repository |

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
