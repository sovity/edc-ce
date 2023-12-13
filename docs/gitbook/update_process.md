Update process
========

**On-premises hosting:**

For the sovity EDC CE, new versions are published via GitHub.
The versioning is subject to the semantic versioning principle, which makes it clear whether a new sovity EDC CE version contains major changes, contains just new functionality or is a patch release.
After a general description of new features and a more technical overview of the changes, deployment migration notes also follow in the case of major changes.
These migration notes must be followed so that the connector version can be updated, if available.
Migration scripts are available for the database that make the data in the database compatible with the new version if there are any changes.

Please also take into consideration which data space your connector is in.
Depending on the data space, there may also be a set mandatory connector version that all participants must adhere to in order to avoid any compatibility problems between connector versions.

Due to the constant developments in the message protocol for connector-to-connector communication, it may happen that a connector version is not compatible with another connector version in the sense that they cannot talk to each other.
Please keep this in mind if you are considering an update and if in doubt, please contact your Data Space operator as to whether an update is technically recommended.

Link to release overview:
https://github.com/sovity/edc-extensions/releases


**Connector as a Service:**

We update the connectors we manage and host ourselves, so you don't have to worry about the technical process itself.
We support multiple connector versions simultaneously in our infrastructure, allowing us to host a specific version depending on the data space requirements.
