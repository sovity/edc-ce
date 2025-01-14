---
icon: objects-column
---

# Dashboard Overview

The start page of your connector is the Dashboard, providing a quick overview of your Connector and its current usage. You can access the Dashboard via:
- Your Connector-UI-URL
- The portal by clicking on ```View frontend``` (if not already in the connector)
- Clicking on ```Dashboard``` in the left-hand navigation bar in your Connector UI.

The Dashboard consists of three sections:
- **Left**: Navigation Menu
- **Center**: Connector KPIs and Properties
- **Right**: General Information about the Connector

Individual details of the Dashboard may vary depending on the version of your own connector used. If you have any questions in this regard, please contact our Customer Service Desk at any time.

![EDC UI Dashboard](/docs/images/edc-ui-dashboard.png)

## Left - Navigation Menu
Navigate through the Connector to access all other pages. You can create an asset, define a contract, or browse through the catalog from here.

## Center - Connector KPIs and Properties

### Upper Section: KPIs and Data Usage
- **Ingoing and outgoing data**: Number of processed data transfers (inbound and outbound)
- **Your contract definitions**: Number of contract definitions in your Connector
- **Your assets**: Number of assets/data sources connected to your Connector
- **Your policies**: Number of usage policies created within your Connector
- **Preconfigured catalogs**: Number of catalogs the Connector is connected to
- **Contract agreements**: Number of contracts your Connector has agreed with partners (inbound & outbound)

### Lower Mid Section: Connector Properties
- **Connector Endpoint**: The Dataspace Protocol endpoint of your Connector.
- **Participant ID/Connector ID**: The unique identifier of your Connector, depending on the ecosystem and its participant identification method.
- **Title**: A human-readable description of the Connector (name).
- **Curator Organisation Name**: The name of your organization.
- **Curator URL**: Links to the curator's homepage.
- **Description**: Additional information about your Connector, such as its purpose.
- **Maintainer Organisation Name**: The name of the maintaining organization, e.g., sovity.
- **Maintainer URL**: Links to the maintainer's homepage.
- **MIW Authority ID**: (Only for MIW Data Spaces) Business Partner Number of the Authority in Data Space.
- **MIW URL**: (Only MIW Data Spaces) Technical property describing the configured DAPS/MIW for your Connector, defining which data spaces you are connected to.
- **MIW/DAPS Token URL**: Technical property indicating where tokens are fetched for your Connector.
- **DAPS JWKS URL**: Technical property describing the configured DAPS JWKS for your Connector, if connected to a DAPS.

## Right - General Information about the Connector
The right-hand section provides general details about the EDC UI. At the top, you can find and copy your Connector endpoint extended by the own participantId as query parameter and additionally the own Management-API URL. Below, you will see a general introduction to the EDC UI.
 
