# Changelog

All notable changes to this project will be documented in this file.

## [x.x.x]

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
