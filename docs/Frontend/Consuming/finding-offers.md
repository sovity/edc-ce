# 1. Finding Data Offers Using the Catalog Browser

To find and consume data from another Connector, you first need to get the Connector Endpoint of the Connector you would like to explore for its offerings. Ensure that both Connectors are registered at the same DAPS/MIW. Note that the EDC does not support consuming its own Contract Definitions!

{% hint style="info" %} The Connector Endpoint is provided on the Dashboard on the bottom as information and additionally in the top right corner. Please be aware, that the Connector Endpoint in in the top right may be extended by the Connector ID, depending on your used Connector version. In some Data Spaces, you can additionally find the Connector Endpoint via the Authority Portal (MDS) or Discovery Service (Catena-X/Cofinity-X). {% endhint %}

![EDC UI Dashboard](/docs/images/edc-ui-dashboard.png)

Once you have the Connector Endpoint of the Connector to explore, click on the tab ```Catalog Browser``` and enter the URL into the ```Connector Endpoint``` search bar. Your Connector will automatically show you all available offerings from the other Connector that your Connector is entitled to see.

You can also connect to multiple Catalogs at once by entering multiple Connector Endpoints, separated by commas, in the search bar.

The information symbol in the ```Connector Endpoint``` search bar provides basic information on the current status of the connection process to the entered catalogs, as well as potential error messages.

The ```Search catalog``` bar lets you search for a specific offering within the Catalog Browser.

{% hint style="info" %} The search bar filters by asset name, keywords, and version number. {% endhint %}

![EDC UI Catalog Browser](/docs/images/edc-ui-catalog-browser.png)

Once you have identified an asset offering of interest, you can click on its name, and a pop-up window with additional information will appear.

![Data Offer in Catalog Browser](/docs/images/edc-ui-offer.png)

If there are several different Contract Offers for an offered Asset, they will be displayed in the detail view.

Click ```Negotiate``` to start negotiating a contract for the desired Contract Offer.

**Accept Data Offer Terms & Conditions**

When clicking ```Negotiate``` You will see a prompt asking you to accept the standard license and policies that come with the contract offer.

![EDC UI Terms & Conditions](/docs/images/edc-ui-tos.png)

- If you haven’t done so already, take a moment to review the  "Standard License" and "Policy" sections in the contract offer. Click anywhere on the screen to go back to the contract offer.
- Once you’ve reviewed everything, click on the checkbox that says  "I agree to the Data Offer Terms & Conditions".
- After you’ve checked the box, click on the "Confirm" button.
Congratulations! You’ve now started the contract negotiation process. Keep an eye out for a notification at the top middle of your screen. This will let you know when your contract negotiation has ended and its result.

{% hint style="warning" %} Contract Negotiations can also end unsuccessfully under certain conditions, for example when contract-policies are not met. {% endhint %}

After a successful negotiation, the data transfer can be started.
 
