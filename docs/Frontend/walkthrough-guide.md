---
icon: file-lines
---

# Walkthrough Guide: From Dashboard to Consuming Data Offers

This walkthrough guide leads you through your first usage of our variant of the Eclipse Dataspace Components Connector (EDC Connector).

When accessing the Connector UI, you'll first see the Connector's dashboard. The dashboard provides an overview of the Connector's performance and properties such as the Connector Endpoint and Participant ID. The navigation bar on the left side of the Connector UI offers direct access to various functions like the Catalog Browser and Transfer History.

![Dashboard](/docs/images/provider-dashboard-1.png)

## Provider: Create Asset

One of the first steps in a Connector is creating an asset. This can be initiated via the “Assets” area in the left navigation bar and pressing the “Create Asset” button. At least the mandatory fields marked with an asterisk should be filled out. The information about the data source must be entered in the `Data offer type` area, as soon as `Offer Type` `Available (with data source)` is selected. Alternatively the offer type `On Request (without data source)` is available. 

![Create new Asset](/docs/images/provider-asset-create-1.png)

## Provider: Create Policy

Optionally, a new policy can be created in the Policies area. The default policies can also be used to later create a contract definition or those that have already been created can be reused. The different policies can also be linked so that for example several must be fulfilled at the same time.

![Create new policy](/docs/images/provider-policy-create-1.png)

## Provider: Publish Data Offer

The last step on the part of the data provider should be to publish a data offer. A unique `Data Offer ID` must be assigned here. An existing policy, for example one created in the previous step, can be set as an `Access Policy` and `Contract Policy`. In addition, at least one asset must be selected that should be offered in the ecosystem.

**Difference between Access Policy and Contract Policy**:
- **Access Policy**: Determines which participants in the ecosystem can see the offer and when.
- **Contract Policy**: Specifies the conditions under which a contract for the data offering may be concluded.

![Create new contract definition](/docs/images/provider-contractdefinition-dataoffer-create-1.png)

## Consumer: Catalog Browser

One of the consumer's first steps is to query the data catalog of another connector. To do this, the Catalog Browser page must be opened in the UI. Another Connector endpoint can then be entered so that its data offers are displayed according where the access-policy-check passed, but please keep in mind, that Catalog requests now require the participantId of the requested counterparty Connector in the UI to be appended. If a suitable data offer has been found, the detail page can be accessed by clicking on the title of the data offer. This page also offers the option of starting a contract negotiation by clicking on the corresponding button when having the detail page opened.

![Catalog Browser](/docs/images/consumer-catalog-browser-1.png)

## Consumer: Contracts

If a previous contract negotiation ends successfully, a contract is concluded. The current contracts can be viewed on the `Contracts` page. In addition to static details about the contract, such as when it was concluded and how many data transfers have already been made, a data transfer can also be initiated by clicking on the existing contract and pressing the transfer button. When initiating the transfer, further information about the data sink must be specified and a decision must be made as to where the data should actually be transferred. The page can be divided into `Consuming` and `Providing` contracts as well as `Active` and `Terminated`.

![Contract Agreement](/docs/images/consumer-contracts-overview-1.png)
