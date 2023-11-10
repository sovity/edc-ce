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

#### Major

#### Minor

- New optional marketing banner for MDS Basic Enterprise Edition Connectors.

#### Patch

- Improved visibility of buttons in "Create New Asset" and "Initiate Transfer"
  Dialogs

#### Deployment Migration Notes

- Enterprise Edition only, MDS variants only: New optional config variable
  `EDC_UI_SHOW_EE_BASIC_MARKETING=true`. Default `false`

## [v2.0.0] 10.10.2023

### Overview

EDC 0 compatible version (Connector UI only).

### Detailed Changes

#### Major

- Switched to semantic versioning
- Migrated transfer history page to the api wrapper
- Migrated contract definition page to the api wrapper
- Migrated policy definition page to the api wrapper
- Migrated asset page to the api wrapper
- Migrated dashboard page to the api wrapper
- The Docker Container now uses the port `8080` instead of ~~`80`~~.

#### Minor

- Added custom 404 pages to connector and broker ui
- New Asset Property "Participant ID"

#### Patch

- Fixed HTTP Parameterization Hints not showing in Asset Details.
- Removed 404-causing login polling from broker UI
- Renamed button from cancel to close in json-dialogs
- Broker: Fixed popularity not logged when clicking on a data offer
- Broker: Fixed missing name in legal notice

#### Deployment Migration Notes

- The Docker Container now uses the port `8080` instead of ~~`80`~~.
- The following ENV Vars were changed:
  - ~~`EDC_UI_DATA_MANAGEMENT_API_URL`~~ became `EDC_UI_MANAGEMENT_API_URL`
  - ~~`EDC_UI_DATA_MANAGEMENT_API_KEY`~~ became `EDC_UI_MANAGEMENT_API_KEY`
- The following ENV Vars were removed and should not be specified anymore:
  - `EDC_UI_CONNECTOR_ID`
  - `EDC_UI_CONNECTOR_NAME`
  - `EDC_UI_CURATOR_ORGANIZATION_NAME`
  - `EDC_UI_CURATOR_URL`
  - `EDC_UI_DAPS_OAUTH_JWKS_URL`
  - `EDC_UI_DAPS_OAUTH_TOKEN_URL`
  - `EDC_UI_IDS_DESCRIPTION`
  - `EDC_UI_IDS_ID`
  - `EDC_UI_IDS_TITLE`
  - `EDC_UI_MAINTAINER_ORGANIZATION_NAME`
  - `EDC_UI_MAINTAINER_URL`
  - `EDC_UI_ASSET_PROP_ORIGINATOR_ORGANIZATION`
  - `EDC_UI_ASSET_PROP_ORIGINATOR`
- New **optional** ENV Vars:
  - `NGINX_ACCESS_LOG`, default: `/dev/stdout`
  - `NGINX_ERROR_LOG`, default: `/dev/stderr`

## [v0.0.1-milestone-8-sovity12] 12.07.2023

### Overview

Broker Server Feature + Bugfix Release

### Detailed Changes

#### Added

- Broker Server: Connector Online Status is now visualized.

#### Fixed

- Fixed Policies not being displayed properly.

## [v0.0.1-milestone-8-sovity11] 07.07.2023

### Overview

Bugfix Release

### Detailed Changes

#### Fixed

- Fixed a bug causing http parameterization not being accessible due to asset
  properties not being persisted on the consumer side.

## [v0.0.1-milestone-8-sovity10] 07.07.2023

### Overview

Bugfix Release

### Detailed Changes

#### Fixed

- Fixed a bug causing data address dtos to be built wrongly.

## [v0.0.1-milestone-8-sovity9] 04.07.2023

### Overview

Full support for parameterized HTTP Data Sources, some Basic EE features.

### Detailed Changes

#### Added

- Parameterization of Http Data Sources.
- Enteprise Edition (Basic): Added support for consuming contract agreement
  limits.

## [v0.0.1-milestone-8-sovity8] 23.06.2023

### Overview

Bugfix release.

### Detailed Changes

#### Fixed

- Broker UI: Fixed sorting not applied.

## [v0.0.1-milestone-8-sovity7] 23.06.2023

### Overview

Build dates, open-ended date intervals, Broker UI MvP features.

### Detailed Changes

#### Added

- Added Connector Build date and Commit Information to Additional Properties
  section in Dashboard
- Added Open-Ended Date Option to Time-Period-Restricted Policies
- Broker UI: Added sorting, filtering and pagination to catalog page.
- Broker UI: Added legal notice page.

## [v0.0.1-milestone-8-sovity6] 06.06.2023

### Overview

Added Broker UI and minor Connector UI improvements.

### Detailed Changes

#### Added

- Added Broker PoC UI:
  - Refactored Module Structure
  - Added Catalog Page
  - Added Connector Page
  - Added Copyright Footer
- Tooltips for Asset Properties that show the asset property names.

#### Fixed

- Fixed Card titles exploding when containing too large words.
- Fixed missing section header for consuming contract agreements.

## [v0.0.1-milestone-8-sovity5] 09.05.2023

### Overview

Removed catalog browser timeouts.

### Detailed Changes

#### Changed

- Catalog Page now has no timeouts.
- Catalog Page now displays partial results.

## [v0.0.1-milestone-8-sovity4] 03.05.2023

### Overview

Bugfixes and minor UI improvements.

### Detailed Changes

#### Added

- Added "Show Details" option to each entry in Transfer History Page

#### Fixed

- Fixed bug in contract definition page that prevented the entire page from
  showing when any contract definition used a non-array operatorRight.
- Fixed contract agreement transfer button being available for providing
  contract agreements.

## [v0.0.1-milestone-8-sovity3] 28.04.2023

### Overview

Reworked Contract Agreement Page, improved stability and full contract offer
information is now displayed.

### Detailed Changes

#### Added

- Reworked the Contract Agreement Page, it now uses its own dedicated API
  Wrapper endpoint.
- Added login polling to prevent auto-logout when page is open.
- Showing all asset properties for assets and contract offers via an "Additional
  Properties" section.
- Showing policies for contract offers in the asset details dialog.

#### Changed

- Marked `EDC_UI_MANAGEMENT_API_URL` as deprecated in favor of
  `EDC_UI_MANAGEMENT_API_URL`.
- Marked `EDC_UI_DATA_MANAGEMENT_API_KEY` as deprecated in favor of
  `EDC_UI_MANAGEMENT_API_KEY`.

#### Removed

- Removed Datasource Payload support, it is probably only intended for Push

#### Fixed

- Fixed getting started docker-compose-yaml to use the newly renamed `edc-dev`
  image.
- Fixed labels of MDS categories and sub-categories.
- Fixed issue when navigating back after clicking logout.

## [v0.0.1-milestone-8-sovity2] 24.03.2023

### Overview

Bugfixes for our productive connectors.

### Detailed Changes

#### Fixed

- Fixed implicit limit of 50 being applied to all views.

## [v0.0.1-milestone-8-sovity1] 20.03.2023

### Overview

Organization names are now prominent in both asset cards and contract offer
cards.

### Detailed Changes

#### Added

- Made asset IDs less prominent in favor of Organization Name

#### Fixed

- Removed password suggestions for ID and related fields in Chrome
- Fixed handling of http error response code 401 when user logout from edc-ui
- Bumped minor dependencies

## [v0.0.1-milestone-7-sovity8] 07.03.2023

### Overview

Minor UI fixes.

### Detailed Changes

#### Changed

- Updated the icon for Fetch Status option in the catalog browser

#### Fixed

- Fixed closing of side nav-bar on pressing escape button
- Fixed "Your Contract Definitions" being called "Your Data Offers" in the
  dashboard.

## [v0.0.1-milestone-7-sovity7] 06.03.2023

### Overview

More Connector Self-Description properties in Dashboard.

### Detailed Changes

#### Added

- Added Connector Self-Description property grid in Dashboard.
- Added info texts to differentiate Connector ID and Connector Endpoint.
- Added support for loading additional config from `EDC_UI_CONFIG_URL` on
  startup.

#### Changed

- Deprecated property `EDC_UI_ASSET_PROP_ORIGINATOR` in favor of
  `EDC_UI_CONNECTOR_ENDPOINT`.
- Deprecated property `EDC_UI_ASSET_PROP_ORIGINATOR_ORGANIZATION_NAME` in favor
  of `EDC_UI_CURATOR_ORGANIZATION_NAME`.

#### Fixed

- Fixed Contract Definition successfully created message.

## [v0.0.1-milestone-7-sovity6] 02.03.2023

#### Changed

- Navigation Item Order: Switched Contract Definitions and Asset Viewer

#### Fixed

- Fixed Contract Definition Page Button Typo.

## [v0.0.1-milestone-7-sovity5] 24.02.2023

### Overview

New contract definition list, catalog status info and Http Datasink fields.

### Detailed Changes

#### Added

- Added Additional Http Datasink properties.
- Catalog Browser now shows if individual Connector Endpoints were unreachable.
- Reworked Contract Definition cards.

#### Changed

- Renamed Connector ID to Connector Endpoint to emphasize differentiate:
  - Connector ID: Configured in certificate, contained in DAT.
  - Connector Endpoint: Configured IDS Endpoint

#### Fixed

- Compatibility section in README.md

## [v0.0.1-milestone-7-sovity4] 20.02.2023

### Overview

New policy list, new http data source properties, UX improvements

### Detailed Changes

#### Added

- Reworked page loading, empty messages and error states
- Reworked policy list, new cards, new detail dialog.
- Additional Http Datasource Properties: method, content type, request body,
  auth header/value, headers

#### Fixed

- Placeholder URLs missing "/control/" path
- Fixed Keyword select not adding keywords on input field blur, causing loss of
  input.
- Fixed transfer dialog submitting on cancel
- Fixed transfer dialog validation not working
- Fix exception on contract definition dialog cancel

## [v0.0.1-milestone-7-sovity3] 06.02.2023

### Fixed

- Fixed dashboard splitting transfers and contract agreements in incoming /
  outgoing. Contract Agreements currently cannot be distinguished as incoming /
  outgoing.

## [v0.0.1-milestone-7-sovity2] 01.02.2023

### Overview

A new dashboard and other quality of life improvements.

### Detailed Changes

#### Added

- Dashboard with KPIs and charts.
- Current Connector Organization & URL on Landing Page / Dashboard
- Simplified adding of data sources in Catalog Browser via Connector ID.
- Source code version of deployed edc-ui can now be accessed under
  /assets/config/version.txt

#### Changed

- Connector URL is now called Connector ID, fixed naming and added good
  placeholders.

#### Fixed

- Fixed additional PolicyDefinition uid vs id issues.
- Fixed E-Mail in README.MD
- Added API Endpoint to workaround extension that fixes an API problem:
  - Could not start transfer processes with just contract agreement ids when
    asset came from a custom catalog provider.
  - It would need the asset's originator url.
  - Since contract agreements don't contain the asset and catalogs are not
    guaranteed to still contain said asset's details due to policies, a new
    endpoint / extension was required.
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

#### Fixed

- Menu of Navigation bar hiding behind feedback widget

## [0.0.1-ab1b6fd0-sovity5] 17.01.2023

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

## [0.0.1-ab1b6fd0-sovity3] 2022-11-10

### Overview

Passing multiple Catalog Urls.

### Detailed Changes

#### Added

- Support for Passing multiple Catalog Urls
  (https://github.com/sovity/edc-sovity-ui/issues/46)
  - `catalogUrl` in `app.config.json` may be set to following pattern:
    {catalog1Url},{catalog2Url},...

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
