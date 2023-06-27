# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - yyyy-mm-dd

### Deployment Migration Notes

### Major

### Minor

### Patch

### Deployment Migration Notes

1. There are new **optional** configuration properties:
    ```yaml
    # Deletion of Connectors after they have been offline for a certain amount of time
    EDC_BROKER_SERVER_DELETE_OFFLINE_CONNECTORS_AFTER=P5D
    EDC_BROKER_SERVER_SCHEDULED_DELETE_OFFLINE_CONNECTORS=0 0 12 ? * *

## [v0.1.0] Broker MvP Release - 2023-06-23

### Overview

Broker MvP using Core EDC MS8.

### Detailed Changes

#### Minor

- Implemented Catalog Page Filters:
    - Data Space Filter
    - Data Category
    - Data Subcategory
    - Data Model
    - Transport Mode
    - Geo Reference Method
- Implemented Catalog Page Sorting:
    - Most Recent
    - By Title
    - By Connector
- Implemented Catalog Page Pagination.

#### Patch

- Fix: Data Offer Filter available values are no longer limited to the selected value if a value is selected.
- Fix: Missing file system vault prevented data space login.
- Fix: Parallel crawling was not actually parallel

### Deployment Migration Notes

1. There are new **required** configuration properties:
    ```yaml
    # List of Data Space Names for special Connectors (default: '')
    EDC_BROKER_SERVER_KNOWN_DATASPACE_CONNECTORS: "Mobilithek=https://some-connector/ids/data,OtherDataspace=https://some-other-connector/ids/data"
    ```
2. There are new **optional** configuration properties available for overriding:
    ```yaml
    # Parallelization for Crawling (default: 3)
    EDC_BROKER_SERVER_NUM_THREADS: 16
   
    # Default Data Space Name (default: MDS)
    EDC_BROKER_SERVER_DEFAULT_DATASPACE: MDS
  
    # Maximum number of Data Offers per Connector (default: 50)
    EDC_BROKER_SERVER_MAX_DATA_OFFERS_PER_CONNECTOR: 50
    
    # Maximum number of Contract Offers per Data Offer (default: 10)
    EDC_BROKER_SERVER_MAX_CONTRACT_OFFERS_PER_DATA_OFFER: 10
   
    # Pagination Configuration: Catalog Page Size (default: 20)
    EDC_BROKER_SERVER_CATALOG_PAGE_PAGE_SIZE: 20
    ```
3. An issue prevented the keystore file from being read, preventing a successful data space log in.
4. Added a reference to [connector/.env](connector/.env) as source for other possible broker server configuration
   options, that have defaults, but might have use cases for overriding.

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:0.1.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity8`
- Sovity EDC CE: [`3.3.0`](https://github.com/sovity/edc-extensions/tree/v3.3.0/connector)

## [v0.0.1] Broker PoC Release - 2023-06-02

### Overview

Initial Broker PoC Release with a minimalistic feature set.

### Detailed Changes

#### Major

- Implemented a Broker PoC with EDC MS8:
    - Periodic Crawling of Connectors
    - Query Data Offers via UI
    - Query Connectors via UI
    - Persistence of Connector Status Updates
    - Persistence of Crawling Execution Times

### Deployment Migration Notes

Please view the [Deployment Section in the README.md](README.md#deployment) for initial deployment instructions.

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:0.0.1`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity6`
- Sovity EDC CE: [`3.3.0`](https://github.com/sovity/edc-extensions/tree/v3.3.0/connector)
