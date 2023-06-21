# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - yyyy-mm-dd

### Deployment Migration Notes

### Major

### Minor

### Patch

- Fix: Data Offer Filter available values are no longer limited to the selected value if a value is selected.

## [v0.0.1] Broker PoC Release - 2023-06-02

Initial Broker PoC Release with a minimalistic feature set.

### Deployment Migration Notes

Please view the [Deployment Section in the README.md](README.md#deployment) for initial deployment instructions.

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:0.0.1`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity6`
- Sovity EDC CE: [`3.3.0`](https://github.com/sovity/edc-extensions/tree/v3.3.0/connector)

### Major

- Implemented a Broker PoC with EDC MS8:
    - Periodic Crawling of Connectors
    - Query Data Offers via UI
    - Query Connectors via UI
    - Persistence of Connector Status Updates
    - Persistence of Crawling Execution Times
