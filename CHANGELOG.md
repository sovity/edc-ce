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

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:{{ CE_VERSION }}`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:{{ UI_VERSION }}`
- Sovity EDC CE: {{ CE Release Link }}

## [v4.0.0] - 2024-03-22

### Overview

Release with adjustmets for the ongoing integration with the Authority Portal

### Detailed Changes

#### Major

- Authority Portal API: Removed deprecated data offer count endpoint

#### Minor

- API: Added endpoint for adding connectors and associated MDS IDs

### Deployment Migration Notes

- Authority Portal API: The deprecated data offer count endpoint was removed:  ~~``authority-portal-api/data-offer-counts``~~.
  Alternatively the connector metadata endpoint should be used: `authority-portal-api/connectors`.

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:4.0.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:3.0.0`
- Sovity EDC CE: [`7.2.2`](https://github.com/sovity/edc-extensions/releases/tag/v7.2.2)

## [v3.5.0] - 2024-02-29

### Overview

Enable better integration of Broker UI and Authority Portal

### Detailed Changes

#### Minor

- Added query params for the connector endpoints filter

#### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:3.5.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:2.5.0`
- Sovity EDC CE: [`7.2.1`](https://github.com/sovity/edc-extensions/releases/tag/v7.2.1)

## [v3.4.0] - 2024-02-27

### Overview

Release to accommodate the Authority Portal release.

### Detailed Changes

#### Minor

- Authority Portal API: Added endpoint for receiving all data offers of registered connectors

#### Patch

- Updated dependency version to have stable Policy (and Contract) identifiers.

### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:3.4.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:2.4.0`
- Sovity EDC CE: [`7.2.1`](https://github.com/sovity/edc-extensions/releases/tag/v7.2.1)

## [v3.3.0] - 2024-02-14

### Overview

MDS bugfix and feature release

### Detailed Changes

#### Minor

- Assets now have new MDS fields

### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:3.3.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:2.4.0`
- Sovity EDC CE: [`7.2.0`](https://github.com/sovity/edc-extensions/releases/tag/v7.2.0)

## [v3.2.0] - 2024-01-18

### Overview

Added validated organization information.

### Detailed Changes

#### Minor

- Validated organization information from the Authority Portal is now displayed
- Authority Portal API: Added endpoint for receiving organization metadata

### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:3.2.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:2.3.1`
- Sovity EDC CE: [`7.1.1`](https://github.com/sovity/edc-extensions/releases/tag/v7.1.1)

## [v3.1.0] - 2023-08-12

### Overview

Re-added deprecated endpoints for Authority Portal API backward compatibility.

### Detailed Changes

#### Minor

- Authority Portal API: Removed data offer count endpoint in favor of new Connector Metadata Endpoint.

### Deployment Migration Notes

_No special migration steps required._

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:3.1.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:2.2.0`
- Sovity EDC CE: [`7.0.0`](https://github.com/sovity/edc-extensions/releases/tag/v7.0.0)

## [v3.0.0] - 2023-06-12

### Overview

EDC 0 / MDS 2.0 bugfix release, Authority Portal API Connector Metadata Endpoint.

### Detailed Changes

#### Major

- Authority Portal API: Removed data offer count endpoint in favor of new Connector Metadata Endpoint.

#### Minor

- Bumped sovity EDC CE to `7.0.0`.
- Bumped Broker UI to `2.2.0`.
- Authority Portal API: Added new Connector Metadata endpoint that includes online status, participant ID and data offer
  counts.

### Deployment Migration Notes

- The DAPS needs to contain the claim `referringConnector=broker` for the broker. The expected value `broker` could be
  overridden by
  specifying a different value for `MY_EDC_PARTICIPANT_ID`.
- Authority Portal API: The data offer count endpoint was removed in favor of the new Connector Metadata
  Endpoint: `authority-portal-api/connectors`, used to be ~~``authority-portal-api/data-offer-counts``~~.

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:3.0.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:2.2.0`
- Sovity EDC CE: [`7.0.0`](https://github.com/sovity/edc-extensions/releases/tag/v7.0.0)

## [v2.0.2] - 2023-11-23

### Overview

EDC 0 Bugfix Release.

### Detailed Changes

#### Patch

- Fixed an issue with the healthcheck.

### Deployment Migration Notes

_No special migration steps required._

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:2.0.2`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:2.1.0`
- Sovity EDC CE: [`6.0.0`](https://github.com/sovity/edc-extensions/releases/tag/v6.0.0)

## [v2.0.1] - 2023-11-20

### Overview

EDC 0 Bugfix Release.

### Detailed Changes

#### Patch

- Fixed an issue preventing DAPS roll-in with the `broker-server-ce` variant.

### Deployment Migration Notes

_No special migration steps required._

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:2.0.1`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:2.1.0`
- Sovity EDC CE: [`6.0.0`](https://github.com/sovity/edc-extensions/releases/tag/v6.0.0)

## [v2.0.0] - 2023-11-17

### Overview

EDC 0 Release, some bugfixes.

### Detailed Changes

#### Major

- Migrated to Eclipse EDC 0.2.1
- Migrated to edc-extensions 5.0.0
- Migrated Assets to JSON-LD

#### Minor

- New Filter: Organization Name
- Search now hits Organization Name

#### Patch

- Fixed some issues with DB Connections not released between tests.
- Fixed issue with initial sorting not being the first sorting.

### Deployment Migration Notes

1. Connectors and Data Offers require an initial crawl before their metadata is filled again.
2. UI Migration Notes since the last Broker Release: https://github.com/sovity/edc-ui/releases/tag/v2.0.0
3. The Protocol Endpoint changed to `https://[MY_EDC_FQDN]/backend/api/dsp`, ~~used to
   be `https://[MY_EDC_FQDN]/backend/api/v1/ids`~~.
4. The Management Endpoint changed to `https://[MY_EDC_FQDN]/backend/api/management`, ~~used to
   be `https://[MY_EDC_FQDN]/backend/api/v1/management`~~.
5. The Connector Endpoint changed to `https://[MY_EDC_FQDN]/backend/api/dsp`, ~~used to
   be `https://[MY_EDC_FQDN]/backend/api/v1/ids/data`~~.

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:2.0.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:2.1.0`
- Sovity EDC CE: [`6.0.0`](https://github.com/sovity/edc-extensions/releases/tag/v6.0.0)

## [v1.2.0] - 2023-10-30

### Overview

Adapt to requirements of the Authority Portal - Release v2.0.0.

### Detailed Changes

#### Minor

- Added an endpoint for getting the data offer amounts for connectors.
- Added a Connector filter to the Catalog Page.

### Deployment Migration Notes

#### Compatible Versions

- Broker Backend Docker Image: `ghcr.io/sovity/broker-server-ce:1.2.0`
- Broker UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity13`
- Sovity EDC CE: [`4.2.0`](https://github.com/sovity/edc-extensions/tree/v4.2.0/connector)

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
        --header 'x-api-key: ApiKeyDefaultValue' \
        --data '["https://some-connector-to-delete/api/dsp", "https://some-other-connector-to-delete/api/dsp"]'
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
        --header 'x-api-key: ApiKeyDefaultValue' \
        --data '["https://some-new-connector/api/dsp", "https://some-other-new-connector/api/dsp"]'
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
    EDC_BROKER_SERVER_KNOWN_DATASPACE_CONNECTORS: "Mobilithek=https://some-connector/api/dsp,OtherDataspace=https://some-other-connector/api/dsp"
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
