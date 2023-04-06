# Changelog

All notable changes to this project will be documented in this file.

## Unreleased

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

### Patch Changes

- Reworked Project, Docker Image and Extension documentations.
- broker-extension: Re-register assets at broker at connector startup
- broker-extension: Added a subsequent resource-id filtering after sparql query, to filter out resources that do not belong to the connector.
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

- Missing blacklist entry for referring connector policy in docker-compose `POLICY_BROKER_BLACKLIST: REFERRING_CONNECTOR`

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
