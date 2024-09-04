---
icon: file-lines
---

# Walkthrough Guide: From Dashboard to Consuming Data Offers

This walkthrough guide leads you through your first usage of our variant of the Eclipse Dataspace Components Connector (EDC Connector).

When accessing the Connector UI, you'll first see the Connector's dashboard. The dashboard provides an overview of the Connector's performance and properties such as the Connector ID and Endpoint. The navigation bar on the left side of the Connector UI offers direct access to various functions like the Catalog Browser and Transfer History.

![Dashboard](/docs/images/edc-ui-walkthrough-dashboard.png)

## Provider: Create Asset

One of the first steps in a Connector is creating an asset. This can be initiated via the “Assets” area in the left navigation bar and pressing the “Create Asset” button. At least the mandatory fields marked with an asterisk should be filled out. Depending on the connector version, more or less information is available to be filled out. The information about the data source must be entered in the Datasource-Information area.

![Create new Asset](/docs/images/edc-ui-walkthrough-create-asset.png)

## Provider: Create Policy

Optionally, a new policy can be created in the Policies area. The default policies can also be used to later create a contract definition or those that have already been created can be reused. The different policies can also be linked so that for example several must be fulfilled at the same time.

![Create new policy](/docs/images/edc-ui-walkthrough-create-policy.png)

## Provider: Create Contract Definition

The last step on the part of the data provider should be to create a contract definition. A unique ID must be assigned here. An existing policy, for example one created in the previous step, can be set as an access policy and contract policy. In addition, at least one asset must be selected that should be offered in the ecosystem. Difference between Access Policy and Crontract Policy: The Access Policy determines which other participants in the ecosystem can see the offer or at what point in time. The Contract Policy determines the conditions under which a contract for the data offering may be concluded.

**Difference between Access Policy and Contract Policy**:
- **Access Policy**: Determines which participants in the ecosystem can see the offer and when.
- **Contract Policy**: Specifies the conditions under which a contract for the data offering may be concluded.

![Create new contract definition](/docs/images/edc-ui-walkthrough-create-contractdefinition.png)

## Consumer: Catalog Browser

One of the consumer's first steps is to query the data catalog of another connector. To do this, the Catalog Browser page must be opened in the UI. Another Connector endpoint can then be entered so that its data offers are displayed according where the access-policy-check passed. If a suitable data offer has been found, the detail page can be accessed by clicking on the title of the data offer. This page also offers the option of starting a contract negotiation by clicking on the corresponding button when having the detail page opened.

![Catalog Browser](/docs/images/edc-ui-walkthrough-catalog-browser.png)

## Consumer: Contracts

If a previous contract negotiation ends successfully, a contract is concluded. The current contracts can be viewed on the Contracts-page. In addition to static details about the contract, such as when it was concluded and how many data transfers have already been made, a data transfer can also be initiated by clicking on the existing contract and pressing the transfer button. When initiating the transfer, further information about the data sink must be specified and a decision must be made as to where the data should actually be transferred. The page is divided into consuming and providing contracts.  In addition, there are different views to see only all active contracts, all terminated contracts or simply all contracts regardless of their status.

![Contract Agreement](/docs/images/edc-ui-walkthrough-contracts.png)
