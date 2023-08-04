# Changelog

All notable changes to this project will be documented in this file.

## [x.x.x] - UNRELEASED

### Major Changes

- Upgrade to core-EDC version `0.1.2`
- Now using the `Dataspace Protocol`
- Major changes to the management API
  - Examples for the new requests are located in the postman collection in the `docs` folder
  - The `OpenAPI` file has been updated to represent the EDC version `0.1.2`

### Migration Notes

1. The `MY_EDC_IDS_BASE_URL` has been renamed to `MY_EDC_PROTOCOL_BASE_URL`
1. The default value of `WEB_HTTP_PROTOCOL_PATH` been changed from `${MY_EDC_BASE_PATH}/api/v1/ids` to `${MY_EDC_BASE_PATH}/api/v1/protocol`
1. New environment variable: `EDC_PARTICIPANT_ID`: `provider`
1. New environment variable: `EDC_JSONLD_HTTPS_ENABLED`: `true`
1. New environment variable: `EDC_DSP_CALLBACK_ADDRESS`: `http://edc:11003/api/v1/protocol`
1. `v1` Management API has been deprecated in favor of the `JSON-LD` `v2` Management API. All endpoints have a `v2` prefix now (example: `http://localhost:11002/api/v1/management/assets/request` is now available at `http://localhost:11002/api/v1/management/v2/assets/request`)

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
  - Sovity EDC CE: `ghcr.io/sovity/edc-ce:4.1.0`
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
  - Sovity EDC CE: `ghcr.io/sovity/edc-ce:4.0.1`
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
  - Sovity EDC CE: `ghcr.io/sovity/edc-ce:4.0.0`
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
