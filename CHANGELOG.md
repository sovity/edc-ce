# Changelog
All notable changes to this project will be documented in this file.

## [X.X.X]

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
