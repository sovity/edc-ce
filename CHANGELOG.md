# Changelog

All notable changes to this project will be documented in this file - formatted and maintained according to the rules
documented on <http://keepachangelog.com>.

This file will not cover changes about documentation, code clean-up, samples, or the CI pipeline. With each version
(respectively milestone), the core features are highlighted. Relevant changes to existing implementations can be found
in the detailed section referring to by linking pull requests or issues.

## [0.0.1-ab1b6fd0-sovity1] 2022-08-31

### Overview

First release of sovity EDC-Data-Dashboard. Adds support for connecting REST-APIs.

### Detailed Changes

#### Added

- sovity Theming

#### Changed

- Sorting Transfer History Entries by Created Date
- Create Asset Dialog
    - Field for arbitrary Data Destination
    - Field for Originator
      - Has to be set to connectors IDS Endpoint, for instance: http://daps-connector-a-controlplane-1:8282/api/v1/ids/data
- Transfer Dialog in Contracts
  - Field for arbitrary Data Destination

#### Removed

- Support for Azure Storage Blobs

#### Fixed
