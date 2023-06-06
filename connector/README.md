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

## Image Variants

The Broker Server is built in differnt variants:

| Docker Image                                                                                                | Type              | Purpose                                                                              | Features                                                                                                         |
|-------------------------------------------------------------------------------------------------------------|-------------------|--------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| [broker-server-dev](https://github.com/sovity/edc-broker-server-extension/pkgs/container/broker-server-dev) | Development       | <ul><li>Local Deployment via our `docker-compose.yaml`</li><li>E2E Testing</li></ul> | <ul><li>Broker Server Extension(s)</li><li>PostgreSQL Persistence & Flyway</li><li>Mock IAM</li></ul>            |
| [broker-server-ce](https://github.com/sovity/edc-broker-server-extension/pkgs/container/broker-server-ce)   | Community Edition | <ul><li>Productive Deployment</li></ul>                                              | <ul><li>Broker Server Extension(s)</li><li>PostgreSQL Persistence & Flyway</li><li>DAPS Authentication</li></ul> |

## Image Tags

| Tag           | Description                       |
|---------------|-----------------------------------|
| latest / main | latest version of our main branch |
| release       | latest release of this repository |

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
