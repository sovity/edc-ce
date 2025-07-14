# 1. Finding Data Offers Using the Catalog Browser

To find and consume data from another Connector, you first need to get the Connector Endpoint of the Connector you would like to explore for its offerings. Ensure that both Connectors are registered at the same Data Space identity provider (DAPS/MIW/...). Note that the EDC does not support consuming its own Data Offers!

{% hint style="info" %} The Connector Endpoint is provided on the Dashboard on the bottom as information and additionally in the top right corner. Please be aware that the Connector Endpoint in the top right is extended by the Participant ID. In some Data Spaces, you can additionally find the Connector Endpoint via the Dataspace Portal or other Discovery Service. {% endhint %}

![EDC UI Dashboard](/docs/images/provider-dashboard-1.png)

Once you have the Connector Endpoint of the Connector to explore, click on the tab `Catalog Browser` and enter the URL into the `Connector Endpoint` search bar. Your Connector will automatically show you all available offerings from the other Connector that your Connector is entitled to see, given the evaluation of the `Access Policy` of the requested Connector, which takes place at the requested Connector upon catalog request.

The information symbol in the `Connector Endpoint` search bar provides basic information on the status of the connection process to the entered catalogs, as well as potential error messages.

The `Search...` bar lets you search for a specific offering within the Catalog Browser.

{% hint style="info" %} The search bar filters by asset name, keywords, and version number. {% endhint %}

![EDC UI Catalog Browser](/docs/images/consumer-catalog-browser-1.png)

Once you have identified an asset offering of interest, you can click on its row, and a detail dialog with additional information will be shown as below.

![Data Offer in Catalog Browser](/docs/images/consumer-dataoffer-properties-1.png)

If there are several different Data Offers for an Asset offered, they will be displayed in the detail view under the tab `Contract Offers`.

Click `Negotiate` to start negotiating a contract for the desired Data Offer.

**Accept Data Offer Terms & Conditions**

When clicking `Negotiate` you will see a prompt asking you to accept the standard license and policies that come with the data offer.

![EDC UI Terms & Conditions](/docs/images/consumer-dataoffer-terms-1.png)

- If you haven’t done so already, take a moment to review the `Standard License` and `Policy` sections in the data offer. Click anywhere on the screen to go back to the data offer details.
- Once you’ve reviewed everything, click on the checkbox that says `I agree to the Data Offer Terms & Conditions`.
- After you’ve checked the box, click on the `Confirm` button.
Congratulations! You’ve now started the contract negotiation process. Keep an eye out for a notification at the top middle of your screen. This will let you know when your contract negotiation has ended and its result.

{% hint style="warning" %} Contract Negotiations can also end unsuccessfully under certain conditions, for example when contract-policies are not met. {% endhint %}

After a successful negotiation, the data transfer can be started.
