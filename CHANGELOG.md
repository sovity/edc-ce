# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - yyyy-mm-dd

### Overview

### Detailed Changes

#### Major

#### Minor

#### Patch

### Deployment Migration Notes

## [v1.1.1] - 2023-10-11

### Overview

Bugfix release for the asset properties issue.

### Detailed Changes

#### Patch

- Fixed a bug causing some string asset properties getting quotes around them.

### Deployment Migration Notes

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:1.1.1`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity13`
- Sovity EDC CE: [`4.2.0`](https://github.com/sovity/edc-extensions/tree/v4.2.0/connector)

## [v1.1.0] - 2023-09-29

### Overview

Bugfix release for the asset properties issue. Also contains the connector delete endpoint.

### Detailed Changes

#### Minor

- New Admin API Endpoint: Delete Connectors

#### Patch

- Fixed a bug causing exceptions when non-string asset properties were used.

### Deployment Migration Notes

1. Connectors can now be dynamically deleted at runtime by using the following endpoint:
    ```shell script
    # Response should be 204 No Content
    curl --request DELETE \
        --url 'http://localhost:11002/backend/api/v1/management/wrapper/broker/connectors?adminApiKey=DefaultBrokerServerAdminApiKey' \
        --header 'Content-Type: application/json' \
        --header 'X-Api-Key: ApiKeyDefaultValue' \
        --data '["https://some-connector-to-delete/api/v1/ids/data", "https://some-other-connector-to-delete/api/v1/ids/data"]'
    ```

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:1.1.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity13`
- Sovity EDC CE: [`4.2.0`](https://github.com/sovity/edc-extensions/tree/v4.2.0/connector)

## [v1.0.3] - 2023-09-01

### Overview

Bugfix Release for the Broker MvP with MS8.

### Detailed Changes

#### Patch

- Fixed sorting the catalog by popularity.

### Deployment Migration Notes

No configuration changes are required.

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:1.0.3`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity13`
- Sovity EDC CE: [`4.2.0`](https://github.com/sovity/edc-extensions/tree/v4.2.0/connector)

## [v1.0.2] - 2023-08-10

### Overview

Bugfix Release for the Broker MvP with MS8.

### Detailed Changes

#### Patch

- Fixed an issue where connector crawling failed when data offer limits were exceeded.
- Fixed searching data offers with capital letters didn't work.

### Deployment Migration Notes

No configuration changes are required.

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:1.0.2`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity12`
- Sovity EDC CE: [`4.1.0`](https://github.com/sovity/edc-extensions/tree/v4.1.0/connector)

## [v1.0.1] Broker MvP Bugfix / Feature Release - 2023-07-12

### Overview

Bugfix / Feature Release for the Broker MvP with MS8: Connectors can now be added at runtime.

### Detailed Changes

#### Major

- Broker Server API now generates into its own Broker Server Client Typescript Library.

#### Minor

- Broker Server API is now part of this repository.
- Dead Connectors are now deleted periodically.
- Connector Online Status is now visualized.
- New Admin API Endpoint: Add Connectors

#### Patch

- Fixed Backend Docker Healthcheck

### Deployment Migration Notes
1. Added new **required** configuration properties:
    ```yaml
    # Broker Server Admin Api Key (required)                                            
    # This is a stopgap until we have IAM
    EDC_BROKER_SERVER_ADMIN_API_KEY: DefaultBrokerServerAdminApiKey
    ```
2. Added new **optional** configuration properties:
    ```yaml
    # CRON interval for crawling ONLINE connectors
    EDC_BROKER_SERVER_CRON_ONLINE_CONNECTOR_REFRESH: "*/20 * * ? * *" # every 20s
    
    # CRON interval for crawling OFFLINE connectors
    EDC_BROKER_SERVER_CRON_OFFLINE_CONNECTOR_REFRESH: "0 */5 * ? * *" # every 5 minutes
    
    # CRON interval for crawling DEAD connectors
    EDC_BROKER_SERVER_CRON_DEAD_CONNECTOR_REFRESH: "0 0 * ? * *" # every hour
    
    # CRON interval for marking connectors as DEAD
    EDC_BROKER_SERVER_SCHEDULED_KILL_OFFLINE_CONNECTORS: "0 0 2 ? * *" # every day at 2am
    
    # Delete data offers / mark as dead after connector has been offline for:
    EDC_BROKER_SERVER_KILL_OFFLINE_CONNECTORS_AFTER: "P5D"
    
    # Hide data offers after connector has been offline for:
    EDC_BROKER_SERVER_HIDE_OFFLINE_DATA_OFFERS_AFTER: "P1D"
    ```
3. Removed **optional** configuration properties:
    ```yaml
    # (Removed) CRON interval for crawling connectors
    EDC_BROKER_SERVER_CRON_CONNECTOR_REFRESH: "0 */5 * ? * *"
    ```
4. Connectors can now be dynamically added at runtime by using the following endpoint:
    ```shell script
    # Response should be 204 No Content
    curl --request PUT \
        --url 'http://localhost:11002/backend/api/v1/management/wrapper/broker/connectors?adminApiKey=DefaultBrokerServerAdminApiKey' \
        --header 'Content-Type: application/json' \
        --header 'X-Api-Key: ApiKeyDefaultValue' \
        --data '["https://some-new-connector/api/v1/ids/data", "https://some-other-new-connector/api/v1/ids/data"]'
    ```
   
#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:1.0.1`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity12`
- Sovity EDC CE: [`4.0.1`](https://github.com/sovity/edc-extensions/tree/v4.0.1/connector)

## [v1.0.0] 

Release was deleted in favor of above release. There was a bug, and we just decided to re-do the release.

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

    # Database Connection Pool Size
    EDC_BROKER_SERVER_DB_CONNECTION_POOL_SIZE: 30
    
    # Database Connection Timeout (in ms)
    EDC_BROKER_SERVER_DB_CONNECTION_TIMEOUT_IN_MS: 30000
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
