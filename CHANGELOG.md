# Changelog

For documentation on how to update this changelog,
please see [changelog_updates.md](docs/dev/changelog_updates.md).


## [x.x.x] - UNRELEASED

### Overview

### Detailed Changes

#### Major Changes

- Moved Catalog Crawler to the Authority Portal

#### Minor Changes

#### Patch Changes

### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:{{ VERSION }}`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:{{ VERSION }}`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:{{ VERSION }}`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:{{ UI VERSION }}`


## [10.4.1] - 2024-09-26

### Overview

MDS Patch release

### Detailed Changes

EDC UI patches only

#### Patch Changes

- Fixed the gaps in renaming "Contract Definition" to "Data Offer"
  ([#831](https://github.com/sovity/edc-ui/issues/831))
- Replaced hints with info boxes in On Request data source
  ([#820](https://github.com/sovity/edc-ui/issues/820))
- Fixed cropping of Contract Offer Ids on catalog browser page
  ([#795](https://github.com/sovity/edc-ui/issues/795))
- Used the `createDataOffer` endpoint to create an asset, policies and a contract definition in a single call
  ([#841](https://github.com/sovity/edc-ui/issues/841))
- Fixed config not being applied properly after a version upgrade
- Fixed Date to DateTime conversion issues when using the operators less than `<=` and greater than `>`
  ([#846](https://github.com/sovity/edc-ui/issues/846))
- Added initial support for UI internationalization
  ([#680](https://github.com/sovity/edc-ui/issues/680))
- Implemented Data Offer wizard wording change request by MDS
- ([PR#850](https://github.com/sovity/edc-ui/pull/850))

### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:10.4.1`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:10.4.1`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:10.4.1`
  - Dev Catalog Crawler: `ghcr.io/sovity/catalog-crawler-dev:10.4.1`
  - Catalog Crawler CE: `ghcr.io/sovity/catalog-crawler-ce:10.4.1`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:4.1.5`


## [10.4.0] - 2024-09-18

### Overview

MDS Patch release

### Detailed Changes

UI and Wrapper API improvements.

#### Minor Changes

- Extend the Wrapper API ([PR 1035](https://github.com/sovity/edc-ce/pull/1035))
  - Adds `createDataOffer` / `pages/create-data-offer` endpoint to create an asset, policies and a contract definition in a single call

#### Patch Changes

- Changed wording on the data offer creation page ([#817](https://github.com/sovity/edc-ui/issues/795))
- Data Offer details now display the contract ID for each contract offer ([#795](https://github.com/sovity/edc-ui/issues/795))
- Warn the user when using an invalid Policy Id ([#746](https://github.com/sovity/edc-ui/issues/746))
- Warn the user when using an invalid Data Offer Id ([#745](https://github.com/sovity/edc-ui/issues/745))
- Fixed time restriction upper bound "local day to datetime" conversion issues
  ([#815](https://github.com/sovity/edc-ui/issues/815))


### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:10.4.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:10.4.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:10.4.0`
  - Dev Catalog Crawler: `ghcr.io/sovity/catalog-crawler-dev:10.4.0`
  - Catalog Crawler CE: `ghcr.io/sovity/catalog-crawler-ce:10.4.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:4.1.4`


## [10.3.0] - 2024-09-04

### Overview

Minor updates for contracts termination

### Detailed Changes

#### Minor Changes

- MDS only
  - Log contract termination events in the LoggingHouse

#### Patch Changes

- EDC CE
  - API request examples updates

- EDC UI
  - Check the contract limits before negotiating a new one.
  - Changed the title of Contract Definitions to Data Offers.
  - Enhanced EDC UI terminologies for the Create Data Offer tab.
  - Date and time display fixes, unified date format.

### Deployment Migration Notes

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:10.3.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:10.3.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:10.3.0`
  - Dev Catalog Crawler: `ghcr.io/sovity/catalog-crawler-dev:10.3.0`
  - Catalog Crawler CE: `ghcr.io/sovity/catalog-crawler-ce:10.3.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:4.1.3`

## [10.2.0] - 2024-08-20

### Overview

API Wrapper update, several bug fixes and database performance improvements.

### Detailed Changes

This is a replacement for redacted release `10.1.0` with a few additional bug fixes.

#### Minor Changes

- API Wrapper:
  - Added wrapper API endpoint to query a single contract agreement

#### Patch Changes

- Core EDC
  - Improve database performance by removing duplicate indexes and using UUID version 7.
- Logginghouse-Client: Update logging-house-client extension to v1.1.0
- EDC Backend
  - Fixed unrestricted policy wrongly displaying error
  - Performance improvement when fetching a single contract agreement
  - The data address is now correctly updated when editing an asset
  - Fix a database initialization error when starting the EDC with Logging House v1.1.0
- EDC UI
  - Copyable contact email and subject fields on asset and data offer detail dialogs
  - Assets Page search input field is now case-insensitive
  - Markdown support for Reference files description, Conditions for use fields
  - Fixed wrong date format when creating a new data offer
  - Temporarily re-implemented the Create Asset Dialog
  - Added description for fields in asset creation mask
  - Added proper handling of custom JSON properties in edit asset process

### Deployment Migration Notes

#### MDS only

##### logging-house-client extension

If the extension is to be switched off, the following must now be set, as the extension is now activated by default when integrated:
- `EDC_LOGGINGHOUSE_EXTENSION_ENABLED: 'false'`

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:10.2.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:10.2.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:10.2.0`
  - Dev Catalog Crawler: `ghcr.io/sovity/catalog-crawler-dev:10.2.0`
  - Catalog Crawler CE: `ghcr.io/sovity/catalog-crawler-ce:10.2.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:4.1.2`

## [10.1.0] - 2024-08-09

### Overview

MDS 2.2 patch release

*Redacted*

This release contained a major bug that prevented the EDC from starting when the logging house and the EDC shared the same database.

This was fixed in 10.2.0

## [10.0.0] - 2024-07-24

### Overview

MDS 2.2 release

### Detailed Changes

#### Major Changes

- Complex policies using AND, OR and XONE:
  - Complex policy support in the Connector UI. 
  - The `UiPolicy` model has been adjusted to support complex expressions including `AND`, `OR` and `XONE`.
  - The `createPolicyDefinition` has been marked as deprecated in favor of the new `createPolicyDefinitionV2` endpoint that supports complex policies.
  - Removed the recently rushed `createPolicyDefinitionUseCase` endpoint in favor of the new `createPolicyDefinitionV2` endpoint.

#### Minor Changes

- Reworked data offer creation page for easier data sharing.
- Both providers and consumers can now terminate their contracts.
- Contracts can be filtered by their termination status.
- Improved "On Request" data offer support in the Connector UI.
- The always-true policy is now created with no constraints instead of the artificial `ALWAYS_TRUE = TRUE` constraint
  - Existing always-true policy definitions are migrated to the new format - existing contract agreements are not affected

#### Patch Changes

- Fixed an issue that caused the auth information to get lost during asset
  creation.

### Deployment Migration Notes

_No special migration notes required_

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:10.0.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:10.0.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:10.0.0`
  - Dev Catalog Crawler: `ghcr.io/sovity/catalog-crawler-dev:10.0.0`
  - Catalog Crawler CE: `ghcr.io/sovity/catalog-crawler-ce:10.0.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:4.1.0`

## [9.0.0] - 2024-07-15

### Overview

MDS 2.2 intermediate release

### Detailed Changes

#### Major Changes

- API Wrapper UI API: Data sources are now well-typed.
- The Broker has been removed in favor of the Authority Portal:
  - A new Deployment Unit, the ["Data Catalog Crawler"](https://github.com/sovity/edc-ce/tree/v9.0.0/extensions/catalog-crawler/README.md), has been added.
  - Each "Data Catalog Crawler" connects to an existing Authority Portal Deployment's DB.
  - Each "Data Catalog Crawler" is responsible for crawling exactly one environment.
  - The Data Catalog functionality of the Broker has been integrated into the Authority Portal.

#### Minor Changes

- Additional ToS check during contract negotiation via the UI.
- "On Request" Data Offers
  - Full support in the API Wrapper UI API
  - Create support in the Connector UI. Full support in the UI is still in progress.
- Added the `sovity-messenger` extension for easy Connector-to-Connector messages.

#### Patch Changes

- Unified database migration histories

### Deployment Migration Notes

- Connector:
  - The database migration system has been moved from multiple migration history tables to a single one.
- Broker:
  - The broker has been removed. For Authority Portal users, please check out the new
    [Data Catalog Crawler Productive Deployment Guide](https://github.com/sovity/edc-ce/tree/v9.0.0/docs/deployment-guide/goals/catalog-crawler-production/README.md).
  - Any previous broker deployment's database is not required anymore.
  - Please care that only some environment variables look similar. It is recommended to create fresh deployments.

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:9.0.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:9.0.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:9.0.0`
  - Dev Catalog Crawler: `ghcr.io/sovity/catalog-crawler-dev:9.0.0`
  - Catalog Crawler CE: `ghcr.io/sovity/catalog-crawler-ce:9.0.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:4.0.0`

## [8.1.0] - 2024-06-14

### Overview

Support for Multiplicity Constraints in the API Wrapper.

### EDC UI

- https://github.com/sovity/edc-ui/releases/tag/v3.2.2

### EDC Extensions and Broker

#### Minor Changes

- API Wrapper
  - Support for Multiplicity Constraints (https://github.com/sovity/edc-ce/issues/968)
  - Providing `Prop` class from `json-and-jsonld-utils` to the java-client to make relevant Constants available

#### Patch Changes

- Postman-collection: Fixed an issue where an API-call was previously wrong in the details of the POST-body.

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:8.1.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:8.1.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:8.1.0`
  - Broker CE: `ghcr.io/sovity/broker-server-ce:8.1.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:3.2.2`

## [8.0.0] - 2024-06-05

### Overview

Starting from version `8`, the Broker has been merged with the Community edition.

[The former changelog](https://github.com/sovity/edc-broker-server-extension/blob/v4.2.0/CHANGELOG.md) for the Broker is still available but will not be updated anymore.

The Broker's version therefore jumps from version 4 to version 8.

The functionalities of each part, Broker and Extensions, on this release, is the same as before the change.

### EDC UI

- https://github.com/sovity/edc-ui/releases/tag/v3.2.2

#### Patch Changes

- Overhaul of the Postman-Collection

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:8.0.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:8.0.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:8.0.0`
  - Broker CE: `ghcr.io/sovity/broker-server-ce:8.0.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:3.2.2`

## [7.5.0] - 2024-05-16

### Overview

Additional Wrapper API features

### EDC UI

- https://github.com/sovity/edc-ui/releases/tag/v3.2.2

### EDC Extensions

#### Minor Changes

- API Wrapper Use Case API: Catalog endpoint

#### Patch Changes

Security updates

### Deployment Migration Notes

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.5.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.5.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.5.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:3.2.2`
- Connector UI Release: https://github.com/sovity/edc-ui/releases/tag/v3.2.2

## [7.4.2] - 2024-04-20

### Overview

MDS Bugfix Release

### Detailed Changes

#### Patch Changes

- Fixed a bug causing Catalog fetches to fail if a data offer with an empty DataModel value existed.
- Fixed naming of the `nutsLocations` field for MDS assets.
- UI: Removed HTTP Verb "HEAD" as it was not supported by the backend
- Docs: Updated image to explain data-transfer-methods
- Docs: Updated documentation for parameterization using [only the UI](https://github.com/sovity/edc-ce/blob/main/docs/getting-started/documentation/parameterized_assets_via_ui.md) or the [Management-API](https://github.com/sovity/edc-ce/blob/main/docs/getting-started/documentation/parameterized_assets.md)
- Docs: Updated [OAuth2 documentation](https://github.com/sovity/edc-ce/blob/main/docs/getting-started/documentation/oauth-data-address.md) about necessary parameters that need to use the vault key instead of providing a secret directly
- Docs: Updated documentation for the [pull-data-transfer](https://github.com/sovity/edc-ce/blob/main/docs/getting-started/documentation/pull-data-transfer.md)
- Dev Utils: Parallel test support for our Test Backend for some requests.

### Deployment Migration Notes

Contains DB migrations, DB backups advised.

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.4.2`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.4.2`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.4.2`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:3.2.2`
- Connector UI Release: https://github.com/sovity/edc-ui/releases/tag/v3.2.2

## [7.4.0] - 2024-04-11

### Overview

MDS bugfixes.

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v3.1.0

#### Minor Changes

- Logginghouse-Client: Add logging-house-client extension 0.2.10
- Migrated MDS fields to mobilityDCAT-AP
- Added a workaround for the assets' parameterization using a fork of the Eclipse EDC 0.2.1

### Deployment Migration Notes

- A new LoggingHouse extension is now included in the EDC CE MDS variant, which means that additional properties must be set for it:
  - `EDC_LOGGINGHOUSE_EXTENSION_ENABLED: "true"`
  - `EDC_LOGGINGHOUSE_EXTENSION_URL: #LoggingHouse URL of the MDS environment`

[EDC UI Migration Notes](https://github.com/sovity/edc-ui/blob/v3.1.0/CHANGELOG.md#v310---2024-04-11)

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.4.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.4.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.4.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:3.1.0`

## [7.3.0] - 2024-03-28

### Overview

Some API Wrapper improvements, some bugfixes.

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v3.0.0

### EDC Extensions

#### Minor Changes

- UIAsset: Replaced unsafe additional and private properties with safer alternative fields `customJsonAsString` (**not** affected by Json LD manipulation) and `customJsonLdAsString` (affected by Json LD manipulation), along with their private counterparts.
- API Wrapper: TS Client Library now supports OAuth Client Credentials
- EDC Backend: Added config variables for remote debugging

#### Patch Changes

- Add a fix for a null pointer exception in the transfer history API.
- Add e2e test for double encoding of query parameters

### Deployment Migration Notes

- EDC UI:
  - New **optional** environment variable: `EDC_UI_MANAGEMENT_API_URL_SHOWN_IN_DASHBOARD` as override for shown Management API URL on the dashboard
- EDC Backend:
  - New **optional** environment variables to enable and configure remote logging & debugging capabilities:
    - `DEBUG_LOGGING = false`
    - `REMOTE_DEBUG = false`
    - `REMOTE_DEBUG_SUSPEND = false`
    - `REMOTE_DEBUG_BIND = 127.0.0.1:5005`

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.3.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.3.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.3.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:3.0.0`

## [7.2.2] - 2024-03-13

### Overview

Bugfix

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v2.4.0

### EDC Extensions

#### Patch Changes

- DspCatalogService: Stable Contract Offer IDs removed

### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.2.2`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.2.2`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.2.2`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:2.4.0`

## [7.2.1] - 2024-02-21

### Overview

Bugfixes

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v2.4.0

### EDC Extensions

#### Patch Changes

- DspCatalogService: Contract Offer IDs are now stable
- Fixed some requests' timeouts by removing the data-plane-instance-store-sql Extension

### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.2.1`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.2.1`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.2.1`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:2.4.0`

## [7.2.0] - 2024-02-14

### Overview

MDS bugfix and feature release

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v2.4.0

#### Minor Changes

- Assets now have new MDS fields

#### Patch Changes

- Docs: Improved documentation of HTTP pull (edc-ui)
- Docs: Add security recommendations for recent API key vulnerabilities
- Fixed connector restricted usage policy
- Fixed connection pool issues by switching to Tractus-X connection pool

### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.2.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.2.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.2.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:2.4.0`

## [7.1.1] - 2024-01-18

### Overview

Bugfix release for minor UI bugs

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v2.3.1

### Deployment Migration Notes

_No special deployment migration steps required_

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.1.1`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.1.1`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.1.1`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:2.3.1`

## [7.1.0] - 2024-01-17

### Overview

MDS feature release: Asset markdown descriptions and editable metadata

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v2.3.0

### EDC Extensions

#### Minor Changes

- Asset metadata is now editable
- Asset descriptions now support Markdown
- Negotiate button is no longer shown for own connector endpoints

### Deployment Migration Notes

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.1.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.1.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.1.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:2.3.0`

## [7.0.0] - 2023-12-06

### Overview

`MY_EDC_PARTICIPANT_ID` must now coincide with a DAT claim.
This fixes the Contract Negotiation issue that affected `5.0.0` and `6.0.0`.

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v2.2.0

### EDC Extensions

#### Major Changes

- Participant IDs must now coincide with a DAT claim.

#### Patch Changes

- Fixed an issue preventing Contract Negotiations.
- Fixed an issue preventing transfer processes from being marked as `COMPLETED` in Eclipse EDC `0.2`.
- Fixed policy and permission targets shown as warnings in the UI.
- Added example for using the API Wrapper to offer and consume data.
- Added CHANGELOG documentation.
- Marked `MY_EDC_NAME_KEBAB_CASE` as deprecated in favor of `MY_EDC_PARTICIPANT_ID`.

### Deployment Migration Notes

- The configured value of `MY_EDC_PARTICIPANT_ID` will now be validated via the DAPS:
  - The configured value of `MY_EDC_PARTICIPANT_ID` must coincide with the claim value `referringConnector`
    as configured for this Connector in the DAPS.
  - For MS8-migrated connectors, if the Participant ID was not configured well before, existing contract agreements
    will stop working. The Participant ID is referenced heavily in counter-party connectors, which makes a migration
    of Participant IDs for old contract agreements impractical.
- If a given data space has no "Participant ID" / "Connector ID" concept or does not use the `referringConnector` claim:
  - It is possible to override the checked claim by overriding `EDC_AGENT_IDENTITY_KEY`.
  - `EDC_AGENT_IDENTITY_KEY` could be set to the claim name of the AKI / SKI Client ID, which should always be part of
    the issued DAT. This would be `sub` for a sovity DAPS and `client_id` for an Omejdn DAPS.
  - `MY_EDC_PARTICIPANT_ID` would have to be set to the AKI / SKI Client ID.
- Renamed ~~`MY_EDC_NAME_KEBAB_CASE`~~ to `MY_EDC_PARTICIPANT_ID`. ~~`MY_EDC_NAME_KEBAB_CASE`~~ continues working, but
  prints a warning on startup if configured.

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:7.0.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:7.0.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:7.0.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:2.2.0`

## [6.0.0] - 2023-11-17

### Overview

Connectors are now pre-configured for the sovity DAPS over Omejdn.

This fixes issues with MDS Connectors not being able to connect to the MDS 2.0.

### EDC UI

https://github.com/sovity/edc-ui/releases/tag/v2.1.0

### EDC Extensions

#### Major Changes

- The default DAPS configuration now supports the sovity DAPS over Omejdn.

#### Patch Changes

- Improved `:extensions:wrapper:wrapper-common-mappers` for broker: `AssetJsonLdUtils`, made some methods public.
- Added example for using the API Wrapper to offer and consume data.

### Deployment Migration Notes

Omejdn DAPS users need to manually add the following Backend ENV Vars:

```yaml
EDC_OAUTH_PROVIDER_AUDIENCE: idsc:IDS_CONNECTORS_ALL
EDC_OAUTH_ENDPOINT_AUDIENCE: idsc:IDS_CONNECTORS_ALL
EDC_AGENT_IDENTITY_KEY: client_id
```

#### Compatible Versions

- Connector Backend Docker Images:
  - Dev EDC: `ghcr.io/sovity/edc-dev:6.0.0`
  - sovity EDC CE: `ghcr.io/sovity/edc-ce:6.0.0`
  - MDS EDC CE: `ghcr.io/sovity/edc-ce-mds:6.0.0`
- Connector UI Docker Image: `ghcr.io/sovity/edc-ui:2.1.0`

## [5.0.0] - 10.10.2023

### Overview

Migration from Eclipse EDC Milestone 8 to Eclipse EDC 0.2.1.

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
- Removed IDS Broker Extension.
- Removed IDS Clearing House Extension.

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
- New modules with utilities for E2E Testing Connectors: `:utils:test-utils` and `:extensions:test-backend-controller`

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
