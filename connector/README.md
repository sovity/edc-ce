<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector:<br />sovity EDC Community Edition</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## sovity EDC Community Edition Docker Images

[Eclipse Dataspace Components](https://github.com/eclipse-edc) (EDC) is a framework
for building dataspaces, exchanging data securely with ensured data
sovereignty.

[sovity](https://sovity.de/) extends the EDC Connector's functionality with extensions to offer
enterprise-ready managed services like "Connector-as-a-Service", out-of-the-box fully configured DAPS
and integrations to existing other dataspace technologies.

We believe in open source and actively contribute to open source community. Our EDC Community Edition packages available
EDC Open Source extensions and combines them with our own open source extensions from this repository to build
ready-to-use EDC Docker Images.

Together with our [EDC UI](https://github.com/sovity/EDC-UI) Docker Images, it offers several of our extended EDC
functionalities for self-hosting purposes.

## Different Image Types

Our EDC Community Edition builds several docker images in different configurations.

| Docker Image                                                                     | Type                     | Purpose                                                                                                                                                 | Features                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
|----------------------------------------------------------------------------------|--------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [edc-dev](https://github.com/sovity/edc-extensions/pkgs/container/edc-dev)       | Devevelopment            | <ul><li>Lightweight local development</li><li>Used in EDC UI's Getting Started section</li></ul>                                                            | <ul><li>Control- and Data-Plane</li><li>sovity Community Edition EDC Extensions</li><li>Management API Auth via API Keys</li><li>Mock IAM</li></ul>                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| [edc-ce](https://github.com/sovity/edc-extensions/pkgs/container/edc-ce)         | sovity Community Edition | <ul><li>Test sovity EDC features</li><li>Self-Deploy EDC</li></ul>                                                                                          | <ul><li>Control- and Data-Plane</li><li>sovity Community Edition EDC Extensions</li><li>Management API Auth via API Keys</li><li>DAPS Authentication</li><li>PostgreSQL Persistence & Flyway</li></ul>                                                                                                                                                                                                                                                                                                                                                                                                                            |
| [edc-ce-mds](https://github.com/sovity/edc-extensions/pkgs/container/edc-ce-mds) | MDS Community Edition    | <ul><li>Demonstrate MDS EDC Features</li><li>Self-Deploy MDS EDC</li></ul>                                                                                  | <ul><li>Control- and Data-Plane</li><li>sovity Community Edition EDC Extensions</li><li>Management API Auth via API Keys</li><li>DAPS Authentication</li><li>PostgreSQL Persistence & Flyway</li><li>Broker Extension</li><li>Clearing House Extension</li></ul>                                                                                                                                                                                                                                                                                                                                                                  |
| edc-ee                                                                           | Commercial               | <ul><li>Productive use</li><li>Professional users</li><li>Our Connector-as-a-Service (CaaS) customers</li><li>[Request Demo](mailto:contact@sovity.de)</ul> | <ul><li>Managed Control- and Data Planes, individually scalable</li><li>Hosted on highly performant infrastructure</li><li>Management API Auth via Service Accounts</li><li>Managed User Auth via standalone IAM (SSO)</li><li>Automatic Dataspace Roll-In, for example to Data Spaces like Catena-X or Mobility Data Space</li><li>Managed DAPS Authentication</li><li>Support &amp; Tutorials</li><li>Automatic updates to newest version and new features</li><li>Off-the-shelf extensions for use cases available</li><li>EDC available within minutes</li><li>Can be combined with Data Space as a Service (DSaaS)</li></ul> |

## Image Tags

| Tag     | Description                                 |
|---------|---------------------------------------------|
| latest  | latest version of our main branch           |
| release | latest release of our EDC Community Edition |

## Configuration

For available configurations please refer to our [Getting Started Guide](../docs/getting-started/README.md).

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
