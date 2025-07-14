---
icon: objects-column
---

# Dashboard Overview

The start page of your connector is the Dashboard, providing a quick overview of your Connector and its current usage. You can access the Dashboard via:
- Directly via your Connector-UI URL
- The sovity hub by clicking on the UI icon next to your Service
- Clicking on `Dashboard` in the left-hand navigation bar in your Connector UI

The Dashboard consists of three sections:
- **Left**: Connector UI Navigation Menu
- **Center**: Connector KPIs and Properties
- **Right**: Information about the Connector, e.g. the `Connector Endpoint + Participant ID` and the `Management API URL`

![EDC UI Dashboard](/docs/images/provider-dashboard-1.png)

## Left - Navigation Menu
Navigate through the Connector to access all other pages. You can create an asset, define a data offer or browse through the catalog from here.

## Center - Connector KPIs and Properties

### Upper Section: KPIs and Data Usage
- **Incoming and Outgoing data**: Number of processed data transfers (inbound and outbound)
- **Your Data Offers**: Number of data offers in your Connector
- **Your Assets**: Number of assets in your Connector
- **Your Policies**: Number of policies created within your Connector
- **Preconfigured Catalogs**: Number of catalogs the Connector is connected to by pre-configuration
- **Contract Agreements**: Number of contracts your Connector has agreed with partners (inbound & outbound)

## Upper Right - General Information about the Connector
The upper right-hand section provides general details about the EDC. You can find and copy your Connector Endpoint extended by the own Participant ID as query parameter and additionally the own Management API URL.

### Lower Section: Connector Properties
- **Connector Endpoint**: The Dataspace Protocol endpoint of your Connector.
- **Participant ID**: The unique identifier of your Connector, depending on the ecosystem and its participant identification method.
- **Title**: A human-readable description of the Connector (name).
- **Connector Version**: The version of you Connector for your reference.
- **Curator Organisation Name**: The name of your organization.
- **Curator URL**: Links to the curator's homepage.
- **Description**: Additional information about your Connector, such as its purpose.
- **Maintainer Organisation Name**: The name of the maintaining organization, e.g., sovity.
- **Maintainer URL**: Links to the maintainer's homepage.
- **MIW Authority ID**: (Only for MIW Data Spaces) Business Partner Number of the Authority in Data Space.
- **MIW URL**: (Only MIW Data Spaces) Technical property describing the configured DAPS/MIW for your Connector, defining which data spaces you are connected to.
- **MIW/DAPS Token URL**: Technical property indicating where tokens are fetched for your Connector.
- **DAPS JWKS URL**: Technical property describing the configured DAPS JWKS for your Connector, if connected to a DAPS.
