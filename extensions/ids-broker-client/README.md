<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />IDS Broker Client Extensions</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues">Request Feature</a>
  </p>
</div>

## About this Extension

This extension communicates changes in connector status and contract definitions to
an [IDS Broker](https://catalog.test.mobility-dataspace.eu/).

It registers / unregisters each EDC Connector on startup / shutdown. It also communicates all contract definition to the
Broker on creation / deletion. It does not communicate contract definitions created with restrictive policies such as
connector restrictions.

## Why does this extension exist?

The Mobility Data Space (MDS) realizes their data space with the centralized catalog approach. They implemented an IDS
Broker and needed an EDC Extension to take over communication.

## Sequence Diagram

[broker-extension.puml](docs/broker-extension.puml)

<img src="./docs/broker-extension.png" alt="EDC IDS Broker Extension Sequence Diagram" width="200" />


### Supported Asset Properties
The Broker Extension supports the following additional meta information to be sent to the broker:
```
- id: "asset:prop:id"
- name: "asset:prop:name"
- contentType: "asset:prop:contenttype"
- description: "asset:prop:description"
- version: "asset:prop:version"
- keywords: "asset:prop:keywords"
- language: 'asset:prop:language'
- publisher: "asset:prop:publisher"
- standardLicense: "asset:prop:standardLicense"
- endpointDocumentation: "asset:prop:endpointDocumentation"

MDS-specific properties:
- dataCategory: 'http://w3id.org/mds#dataCategory'
- dataSubcategory: 'http://w3id.org/mds#dataSubcategory'
- dataModel: 'http://w3id.org/mds#dataModel'
- geoReferenceMethod: 'http://w3id.org/mds#geoReferenceMethod'
- transportMode: 'http://w3id.org/mds#transportMode'
```

## Getting Started

Check out the [Getting Started](https://github.com/sovity/edc-extensions/tree/main/connector#getting-started) section of
our EDC Community Edition.

Our EDC Community Edition is built with both the clearing house and broker extensions and is ready to be used in the
Mobility Data Space (MDS).

## Configuration

The Broker URL can be configured with the ENV Var:

```dotenv
EDC_BROKER_BASE_URL=https://broker.test.mobility-dataspace.eu
```

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
