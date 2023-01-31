# Changelog

All notable changes to this project will be documented in this file - formatted
and maintained according to the rules documented on <http://keepachangelog.com>.

This file will not cover changes about documentation, code clean-up, samples, or
the CI pipeline. With each version (respectively milestone), the core features
are highlighted. Relevant changes to existing implementations can be found in
the detailed section referring to by linking pull requests or issues.

## Unreleased

### Overview

### Detailed Changes

#### Added

- Dashboard with KPIs and charts.
- Current Connector Organization & URL on Landing Page / Dashboard
- Simplified adding of data sources in Catalog Browser via Connector ID.

#### Changed

- Connector URL is now called Connector ID, fixed naming and added good placeholders.

#### Removed

#### Fixed

- Fixed additional PolicyDefinition uid vs id issues.
- Fixed E-Mail in README.MD
- Added API Endpoint to temporary extension to fix an API problem of not being
  able to start transfer proceses with just contract agreement ids.
- Fixed MDS Logo not working.

## [v0.0.1-milestone-7-sovity1] 19.01.2023

### Overview

- Prepared this repository for its open source release:
  - Better configuration via ENV Vars, documentation and CI.
  - Changed versioning system to `$EDC_VERSION-sovity$EDC_UI_MINOR_VERSION`.
  - Changed release image to `ghcr.io/sovity/edc-ui`.

### Detailed Changes

#### Added

- Split mds and sovity profiles each into "open source" and "hosted by sovity"
  variants.

#### Changed

- Releasing images now as `ghcr.io/sovity/edc-ui`.
- Changed configuration:
  - Removed `app.config.json`.
  - Configuration via `EDC_UI_` environment variables in both local dev and
    docker container.
  - See `app-config-properties.ts` for available properties.
  - Configuration via single environment variable `EDC_UI_CONFIG_JSON` possible.
- Added prettier as code formatter.
  - Formatted all non-generated code with prettier.

#### Removed

#### Fixed

- Menu of Navigation bar hiding behind feedback widget

## [0.0.1-ab1b6fd0-sovity5] 17.01.2023

### Overview

### Detailed Changes

#### Added

- Added support for connector restricted usage policy.
- Added additional asset fields:
  - General fields (e.g. standard license).
  - MDS specific fields (e.g. transport mode).
- Catalog Browser:
  - Reworked cards to support to support some new fields.
  - Added new detail dialog showing asset details.
- Asset Viewer:
  - Reworked cards to support to support some new fields.
  - Added new detail dialog showing asset details.
  - Added field CONNECTOR ORIGINATOR ORGANIZATION to be fetched from
    app.config.json
- Asset Create Dialog:
  - Divided asset creation into stages with an Angular Material Stepper.
  - Added title and fixed styling.
  - Added validation for required fields.
  - Added validation for URL fields.
  - Added validation for ID field, no whitespaces.
  - Added ID generation from name.
  - Added vocabulary for MDS specific fields: Data Category, Data Subcategory,
    Transport Mode.
  - Added new MDS field: Data Subcategory
- Policy Definition Dialog:
  - Added title and fixed styling.
  - Added validation for required fields.
- Contract Definition Dialog:
  - Added title and fixed styling.
  - Added validation for required fields.

#### Changed

- Changed asset properties, especially ID property. See `asset-properties.ts`.

#### Removed

#### Fixed

- Error-Message Displayed when Creating and Cancelling the Create-Policy-Dialog
- Removed dead theming code at wrong places.
- Fixed "black" accent color having black text on black background.
- Asset Create Dialog:
  - Fixed validation not preventing submit.
  - Fixed data flow so submit errors don't close dialog.
- Policy Viewer
  - Fixed filter / pagination bar styling.
- Policy Definition Dialog:
  - Fixed validation not preventing submit.
  - Fixed data flow so submit errors don't close dialog.
- Contract Definition Viewer
  - Fixed filter / pagination bar styling.
- Contract Definition Dialog:
  - Fixed validation not preventing submit.
  - Fixed data flow so submit errors don't close dialog.

## [0.0.1-ab1b6fd0-sovity4] 04.01.2023

### Overview

Supporting communication with milestone 7 EDC. Working Logout.

### Detailed Changes

#### Added

- Angular 14 and Angular Material 14
- Add Help-Box (Zammad links)
  (https://github.com/sovity/edc-sovity-ui/issues/55)
- Additional `logoutUrl` Property in app.config.json
  (https://github.com/sovity/edc-sovity-ui/issues/24)
  - OAuth2-Proxy-Config has to be adapted
    - Setting: --whitelist-domain to {{KEYCLOAK_ASE_URL}}
  - Schema: {{EDC_UI_BASE_URL}}/oauth2/sign_out?rd={{KEYCLOAK_LOGOUT_URL}}
  - KEYCLOAK_LOGOUT_URL
    - all url encoded including {{KEYCLOAK_BASE_URL}} and {{EDC_UI_BASE_URL}}
    - Schema:
      {{KEYCLOAK_BASE_URL}}%2Frealms%2Fsovity%2Fprotocol%2Fopenid-connect%2Flogout%3Fclient_id%3D{{OAUTH2_PROXY_KEYCLOAK_CLIENT_ID}}%26post_logout_redirect_uri%3D{{EDC_UI_BASE_URL}}
- Support customizing the NGINX listen address
  (https://github.com/sovity/edc-sovity-ui/issues/42)
  - Required Environment-Variables
    - NGINX_BIND
    - NGINX_PORT
- Added secret scanning to repo
- Added MDS logo in the tool bar and added MDS theme
- Added focus on nav-bar item
- Added test server setup
- Added Catalog Url Field in Catalog Browser
  (https://github.com/sovity/edc-sovity-ui/issues/83)

#### Changed

- Renamed policyDefinition `uid` field to `id`
- Removed Logout-Button from Startpage and added it to the menu
  (https://github.com/sovity/edc-sovity-ui/issues/24)

#### Removed

- Developer text description on start page

#### Fixed

## [0.0.1-ab1b6fd0-sovity3] 2022-11-10

### Overview

Passing multiple Catalog Urls.

### Detailed Changes

#### Added

- Support for Passing multiple Catalog Urls
  (https://github.com/sovity/edc-sovity-ui/issues/46)
  - `catalogUrl` in `app.config.json` may be set to following pattern:
    {catalog1Url},{catalog2Url},...

#### Changed

#### Removed

#### Fixed

## [0.0.1-ab1b6fd0-sovity2] 2022-09-28

### Overview

Robustness and convenience improvements.

### Detailed Changes

#### Added

- Field `originator` in `app.config.json`
  (https://github.com/sovity/edc-sovity-ui/pull/36)

#### Changed

- CreateAssetDialog (https://github.com/sovity/edc-sovity-ui/pull/31)
  - Added ComboBox for Asset Datasource-Type
    - Json: Passing a Datasource using Json
    - Rest-Api: Just passing a Url
  - Removed Originator Field
  - Originator will now bet set using the `originator` from `app.config.json`
  - Changed Json Input Field to TextArea
- TransferDialog (https://github.com/sovity/edc-sovity-ui/pull/33)
  - Added ComboBox for Transfer Destination-Type
    - Json: Passing a Data-Destination using Json
    - Rest-Api: Just passing a Url
- PolicyDialog (https://github.com/sovity/edc-sovity-ui/pull/34)
  - Removed fields (Assignee, Assigner, Permissions, Prohibitions and
    Obligations)
  - Added ComboBox for Choosing a fixed Policy
  - Added Time-Interval-Selection Component for Time-Restricted Policy

#### Removed

- `Deprovision` Button in Transfer History: Has been used for AzureBlob Storage
  before, but is not required for Rest-Api Transfers
  (https://github.com/sovity/edc-sovity-ui/pull/35)

#### Fixed

- User-Input will be trimmed before sending it to the EDC-Backend
  (https://github.com/sovity/edc-sovity-ui/pull/39)

## [0.0.1-ab1b6fd0-sovity1] 2022-08-31

### Overview

First release of sovity EDC-Data-Dashboard. Adds support for connecting
REST-APIs.

### Detailed Changes

#### Added

- sovity Theming

#### Changed

- Sorting Transfer History Entries by Created Date
- Create Asset Dialog
  - Field for arbitrary Data Destination
  - Field for Originator
    - Has to be set to connectors IDS Endpoint, for instance:
      http://daps-connector-a-controlplane-1:8282/api/v1/ids/data
- Transfer Dialog in Contracts
  - Field for arbitrary Data Destination

#### Removed

- Support for Azure Storage Blobs

#### Fixed
