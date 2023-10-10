# Changelog

All notable changes to this project will be documented in this file.

## [x.x.x] - UNRELEASED

### Overview

### EDC UI

### EDC Extensions

#### Major Changes

#### Minor Changes

### Deployment Migration Notes

#### Compatible Versions

## [5.0.0] - 10.10.2023

### Overview

Migration from Eclipse EDC Milestone 8 to Eclispe EDC 0.2.1.

The API Wrapper and API Client Libraries can now be used to fully control a sovity EDC Connector.

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v2.0.0

### EDC Extensions

#### Major Changes

- Bump Eclipse EDC Version to `0.2.1`:
  - Now using the Data Space Protocol (DSP) over the ~~IDS Protocol~~.
  - Major changes to the Management API. See the postman collection / OpenAPI file.
- The Getting Started Docker Compose file is no longer to be used as reference for deployments:
  - The Getting Started Docker Compose file now launches connectors for local demo purposes.
  - For productive deployments, a detailed deployment guide has been added.
  - The Dev-Images now also require a PostgreSQL Database.

#### Minor Changes

- All Connector UI Endpoints were migrated to our UI API Wrapper. New UI API Wrapper Endpoints:
    - Asset Page
    - Create Asset
    - Delete Asset
    - Catalog / Data Offers
    - Contract Definition Page
    - Contract Negotiation Start / Detail
    - Create Contract Definition
    - Delete Contract Definition
    - Policy Definition Page
    - Create Policy Definition
    - Delete Policy Definition
    - Dashboard Page
- New modules with common UI models and mappers for the Connector UI and Broker UI: `:extensions:wrapper:wrapper-common-api` and `:extensions:wrapper:wrapper-common-mappers`.
- New module with centralized Vocab and utilities for dealing with EDC / DCAT JSON-LD: `:utils:json-and-jsonld-utils`
- New module with utilities for parsing DCAT Catalog responses for use in the UI API Wrapper and the Broker Server: `:utils:catalog-parser`
- New modules with utilities for E2E Testing Connectors: `:utils:test-connector-remote` and `:extensions:test-backend-controller`

#### Patch Changes

- New modules in `:launchers:common` and `:launchers:connectors` so building different variants no longer requires separate builds.
- New module `:extensions:wrapper:wrapper-api` split from `:extensions:wrapper:wrapper` so integration tests in `wrapper` can use the Java Client Library.
- New JUnit E2E Tests in `:launchers:connectors:sovity-dev` that start two connectors and test the data exchange.

### Deployment Migration Notes

1. Deployment Migration Notes for the EDC UI: https://github.com/sovity/edc-ui/releases/tag/v2.0.0
2. The Connector Endpoint changed to `https://[FQDN]/api/dsp` from ~~`https://[FQDN]/api/v1/ids/data`~~. 
3. The Management Endpoint changed to `https://[FQDN]/api/management` from ~~`https://[FQDN]/api/v1/management`~~.
4. The `v1` Eclipse EDC Management API has been replaced by the Eclipse EDC `JSON-LD` `v2` Management API. Our Postman Collection shows some example requests. 
   However, a switch to our [API Wrapper](extensions/wrapper/README.md) is recommended. Despite our Use Case API Wrapper API still being in development, 
   the Connector UI API Wrapper is fully functional and can be used in concatenation with our type-safe generated API Client Libraries to both provide and 
   consume data offers.
5. The Connector now uses the Data Space Protocol (DSP) instead of the IDS Protocol. This requires different paths to be available from the internet. 
   Please refer to our deployment guide for more information.
6. If the old protocol endpoint required HTTP communication to pass as a workaround for a certain bug, this should be undone now, 
   with all protocol endpoints being secured by HTTPS/TLS.

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:5.0.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:5.0.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:5.0.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:2.0.0`

## [4.2.0] - 2023-09-01

### Overview

MDS 1.2 release using MS8 EDC.

### EDC UI

- https://github.com/sovity/edc-ui/releases/tag/v0.0.1-milestone-8-sovity13

### Detailed Changes

#### Patch Changes

- Fixed issues with Broker Client Extension causing exceptions, because the MDS no longer uses the legacy broker.

### Deployment Migration Notes

#### Compatible Versions

- Connector Backend Docker Images:
    - Dev EDC: `ghcr.io/sovity/edc-dev:4.2.0`
    - sovity EDC CE: `ghcr.io/sovity/edc-ce:4.2.0`
    - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:4.2.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity13`

## [4.1.0] - 2023-07-24

### Overview

Security improvements of container image and enhancements for the `ReferringConnectorValidationExtension`.

### EDC UI

- https://github.com/sovity/edc-ui/releases/tag/v0.0.1-milestone-8-sovity12

### EDC-Extensions

#### Minor Changes
- ReferringConnectorValidationExtension: Added support for comma separated lists of connectors using the EQ operator as well as pure Lists using the IN operator.

#### Patch Changes
- Automatically delete old transfer-processes if there are more than 3000 entries in the transfer-process-table
- Change base-image to `eclipse-temurin:17-jre-alpine`
- Run java process with a non-root user

### Deployment Migration Notes
- `default` datasource has to be added
  - `EDC_DATASOURCE_DEFAULT_NAME`=default
  - `EDC_DATASOURCE_DEFAULT_URL`=jdbc:postgresql://connector:5432/edc
  - `EDC_DATASOURCE_DEFAULT_USER`=user
  - `EDC_DATASOURCE_DEFAULT_PASSWORD`=password

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:4.1.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:4.1.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:4.1.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity12`

## [4.0.1] - 2023-07-07

### Overview

Bugfixes regarding Parameterized Http Datasource Support and open-ended date intervals.

### EDC UI

- https://github.com/sovity/edc-ui/releases/tag/v0.0.1-milestone-8-sovity11

### EDC-Extensions

#### Patch Changes

- Bumped EDC UI Version

### Deployment Migration Notes

No changes besides docker image versions.

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:4.0.1`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:4.0.1`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:4.0.1`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity11`

## [4.0.0] - 2023-07-05

### Overview

Parameterized Http Datasource Support and open-ended date intervals.

### EDC UI

- https://github.com/sovity/edc-ui/releases/tag/v0.0.1-milestone-8-sovity9

### EDC-Extensions

#### Major Changes

- Removed Contract Agreement Transfer API Extension in favor of new API Wrapper UI Endpoint.
- Removed Broker-Server APIs.

#### Minor Changes

- UI API: Added support for parameterized HTTP Data Sources.
- Broker-/ClearingHouse-Client: The extensions can be dynamically enabled and disabled via properties (see
  getting-started Readme FAQ section).
- Broker Server API: New API Endpoint `DataOfferDetailPage` and `ConnectorDetailPage` with model.

### Deployment Migration Notes

No changes besides docker image versions.

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:4.0.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:4.0.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:4.0.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity9`

## [3.3.0] - 2023-06-06

### Minor Changes

- Added build dates to Last Commit Info Extension
- Added Transfer History Page model to API Wrapper.
- Finalize Broker Server API for PoC.

### Patch Changes

- Minor EE API adjustments.


## [3.2.0] - 2023-05-17

### Minor Changes

- API Wrapper now supports OAuth2 Client Credentials Auth.
- API Wrapper now contains initial Broker Server API Spec.
- API Wrapper now contains initial File Storage Enterprise Edition API Spec.
- API Wrapper Contract Agreement Page Cards now contain Contract Negotiation IDs.

### Patch Changes

- Bumped EDC UI version to `ghcr.io/sovity/edc-ui:0.0.1-milestone-8-sovity5` in production `docker-compose.yaml`. This
  fixes a CORS-related issue.

## [3.1.0] - 2023-04-27

### Minor Changes

- feat: wrapper contract agreement api

### Patch Changes

- wrapper: added contractAgreements- and transferProcessesCounts
- fix: broker extension provides empty fields
- fix: update postman collection
- bump org.junit.jupiter:junit-jupiter-api from 5.9.2 to 5.9.3

## [3.0.1] - 2023-04-06

### Fixed

- Wrong image tag in env file
- `EDC_IDS_ENDPOINT` was not set correctly on image build

### Changed

- Reverted docker-compose.yaml to run only one connector

## [3.0.0] - 2023-04-04

### Major Changes

- Changed EDC Docker Image Variants to `edc-dev`, `edc-ce` and `edc-ce-mds`.
- Changed Java Maven Artifact GroupIds to `de.sovity.edc.ext` and `de.sovity.edc`
- Renamed `broker` to `ids-broker-client`.
- Renamed `clearinghouse` to `ids-clearinghouse-client`.

### Minor Changes

- EDC API Wrapper + EDC API Client Bootstrap
- Added Docker Image Tag `release` for latest releases.
- Added sovity Minimal Extension Package.
- broker-extension: Re-register assets at broker at connector startup

### Patch Changes

- Reworked Project, Docker Image and Extension documentations.
- broker-extension: Re-register assets at broker at connector startup
- broker-extension: Added a subsequent resource-id filtering after sparql query, to filter out resources that do not
  belong to the connector.
- bump org.openapi.generator from 6.3.0 to 6.5.0
- bump io.quarkus from 2.16.4.Final to 2.16.6.Final
- bump io.quarkus.platform:quarkus-bom from 2.16.5.Final to 2.16.6.Final
- bump io.swagger.core.v3.swagger-gradle-plugin
- bump io.swagger.core.v3:swagger-annotations-jakarta
- bump io.swagger:swagger-annotations from 1.6.8 to 1.6.10

## [2.0.3] - 2023-03-24

### Fixed

- Bug in postman collection, ports needed to be updated due to release 2.0.2.

## [2.0.2] - 2023-03-23

### Fixed

- Bug in migration scripts, for existing contract negotiations the embedded JSON array of contract offers was missing
  contractStart, contractEnd.

## [2.0.1] - 2023-03-21

### Fixed

- Bug in migration scripts, default values are now set.

## [2.0.0] - 2023-03-20

### Fixed

- Missing blacklist entry for referring connector policy in
  docker-compose `POLICY_BROKER_BLACKLIST: REFERRING_CONNECTOR`

### Changed

- Updated to EDC-Connector 0.0.1-milestone-8.

## [1.5.1] - 2023-03-17

### Fixed

- Changed docker-compose file to use released instead of latest versions of EDC-Connector and EDC-UI

## [1.5.0] - 2023-03-07

### Feature

- `EDC_FLYWAY_REPAIR=true` variable can now be set to run flyway repair when migrations failed

## [1.4.0] - 2023-03-06

### Feature

- EDC UI Config Extension

## [1.3.0] - 2023-02-27

### Feature:

- Last Commit Info Extension
- Persistence into PostgreSQL Database

### Fixed:

- add if-else switch to get_client.sh for AKI `keyid` keyword
- Set _test_ as default MDS environment (in docs and docker-compose)
- Updated ports of Postman collection json file
- Added unregister connector to puml diagram
- Cannot fetch own catalog due to wrong port mapping

## [1.2.0] - 2023-02-02

### Feature:

- Add setting `POLICY_BROKER_BLACKLIST` to blacklist policies from being published to broker
- ContractAgreementTransferApi-Extension: Providing an endpoint to start a data-transfer for a contract-agreement

### Changed

- Extend get_client script to add support for OpenSSL version 3.x

## [1.1.0] - 2023-01-23

### Feature:

- Add additional meta information to resource payload when publishing to broker
- Add connector description to broker message
- Add time-interval and participant based policies
- Add ClearingHouse Extension

### Changed

- Modified module structure to have only one Boker and one ClearingHouse Extension
- Bump junit-jupiter-api from 5.8.1 to 5.9.2
- Bump junit-jupiter-engine from 5.8.1 to 5.9.2
- Bump com.github.johnrengelman.shadow from 7.0.0 to 7.1.2

## [1.0.1] - 2023-01-11

### Fixed:

- Connector not registering to broker due to null pointer exception
- Set dev as default environment (in docs and docker-compose)

### Feature:

- Add ski/aki and jks extraction script

## [1.0.0] - 2022-12-19

- initial release
