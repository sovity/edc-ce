# 1. Create Data Offer

With the latest Connector frontends, instead of first creating an `Asset`, then a `Policy` and then a `Data Offer` that links the two, it is possible to use a new flow that does all at once, the `New Data Offer` flow. This flow starts with the definition of the data source behind the asset. This is followed by the asset details, such as the name and description as metadata for the asset. Finally, you have the choice of how the asset should be published in the data space. As soon as everything is filled in and the `Publish` button is pressed, a data offer is created that publishes the asset in the data space under the specified policies. Depending on the connected dataspace and variant of the Connector, the fields may change, and additional fields may be required for filling in or additional options may be visible to select.

To familiarize yourself with the individual details of the settings and fields, please refer to the detailed instructions for the individual areas represented in the flow:
- Create Asset
- Create Policy
- Create Data Offer

![Create Data Offer](/docs/images/provider-contractdefinition-dataoffer-create-flow-1.png)
