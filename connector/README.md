<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector:<br />Sovity EDC Community Edition</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues">Request Feature</a>
  </p>
</div>

## Our EDC Community Edition Docker Images

[Eclipse Dataspace Components](https://github.com/eclipse-edc) is a framework
for building dataspaces, exchanging data securely with ensured data
sovereignity.

[sovity](https://sovity.de/) extends the EDC Connector's functionality with extensions to offer
enterprise-ready managed services like "Connector-as-a-Service", out-of-the-box fully configured DAPS
and integrations to existing other data space technologies.

We believe in open source and actively contribute to open source community. Our EDC Community Edition packages our
EDC Extensions with other open source EDC Extensions to build ready-to-use Docker Images.

Together with our [EDC UI](https://github.com/sovity/EDC-UI) Docker Images it offers several of our extended EDC
functionalities for self-hosting or demonstrative purposes.

## Image Variants

Our EDC Community Edition builds several docker images in different configurations.

Check which one you are currently using / which one you need.

| Docker Image                                                                     | Type              | Built For                                                                                                                                                   | Features                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
|----------------------------------------------------------------------------------|-------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [edc-dev](https://github.com/sovity/edc-extensions/pkgs/container/edc-dev)       | Dev               | <ul><li>Lightweight local dev</li><li>EDC UI's Getting Started uses this</li></ul>                                                                          | <ul><li>Control- and Data-Plane</li><li>Sovity Community Edition EDC Extensions</li><li>Management API Auth via API Keys</li><li>Mock IAM</li></ul>                                                                                                                                                                                                                                                                                                                                                                                               |
| [edc-ce](https://github.com/sovity/edc-extensions/pkgs/container/edc-ce)         | Community Edition | <ul><li>Demonstrate Sovity EDC Features locally</li><li>Self-Deploy EDC</li></ul>                                                                           | <ul><li>Control- and Data-Plane</li><li>Sovity Community Edition EDC Extensions</li><li>Management API Auth via API Keys</li><li>OAUTH2 Auth</li><li>PostgreSQL Persistence & Flyway</li></ul>                                                                                                                                                                                                                                                                                                                                                    |
| [edc-ce-mds](https://github.com/sovity/edc-extensions/pkgs/container/edc-ce-mds) | Community Edition | <ul><li>Demonstrate MDS EDC Features</li><li>Self-Deploy MDS EDC</li></ul>                                                                                  | <ul><li>Control- and Data-Plane</li><li>Sovity Community Edition EDC Extensions</li><li>Management API Auth via API Keys</li><li>OAUTH2 Auth</li><li>PostgreSQL Persistence & Flyway</li><li>Broker Extension</li><li>Clearing House Extension</li></ul>                                                                                                                                                                                                                                                                                          |
| edc-control-plane <br />edc-data-plane                                           | Commercial        | <ul><li>Productive use</li><li>Professional users</li><li>Our Connector-as-a-Service (CaaS) customers</li><li>[Request Demo](mailto:contact@sovity.de)</ul> | <ul><li>Managed Control- and Data Planes, individually scalable</li><li>Hosted on highly performant infrastructure</li><li>Managed User IAM</li><li>Automatic Dataspace Roll-In, for example to Data Spaces like Catena-X or Mobility Data Space</li><li>Managed DAPS available</li><li>Support &amp; Tutorials</li><li>Automatic updates to newest version and new features</li><li>Off-the-shelf extensions for use cases available</li><li>EDC available within minutes</li><li>Can be combined with Data Space as a Service (DSaaS)</li></ul> |

## Image Tags

| Tag     | Description                                             |
|---------|---------------------------------------------------------|
| latest  | latest version of our main branch                       |
| release | latest release version of our EDC Extensions repository |

## Configuration

For available configurations please refer to:

- Our [Getting Started Section](../README.md#getting-started)
- Our Getting Started [docker-compose.yaml](../docker-compose.yaml)
- The [.env-File](.env) of defaults for our Docker Images.
- The EDC and EDC Extensions themselves

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
