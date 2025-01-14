# Data Transfer Process

To transfer the data to your desired data sink, navigate to the ```Contracts``` page. This page displays all your contract agreements, including both consuming and providing contract agreements. You can easily distinguish between them by the arrow next to the Contract Agreement (Up = providing, Down = consuming). You can inspect data on your contract agreements, such as the counterpart Connector.

## Initiating a Data Transfer

1. **Selecting a Contract Agreement**
   - Click on a consuming contract agreement to open a pop-up window displaying details of the contract agreement.
  
![Contract Agreement](/docs/images/edc-ui-contracts.png)

2. **Defining Data Sink Properties**
   - Click on ```Transfer``` to define your data sink properties.

![Initiating data transfer](/docs/images/edc-ui-transfer.png)

### Supported Data Sink Types

There are three data sink types supported:

1. ```REST-API Endpoint```
2. ```Custom Datasink Config (JSON)```
3. ```Custom Transfer Process Request (JSON)```

#### 1. Transfer to a REST-API Endpoint

To transfer data to a REST-API endpoint, select an HTTP method and provide the URL of the data sink. You can also add additional headers, such as for authentication.

![Datasink properties](/docs/images/edc-ui-transfer-dialog.png)

**Parameterization**

{% hint style="info" %} The HttpData-Push transfer-type no longer supports parameterization in the latest connector versions, so this functionality will soon no longer be available from a consumer perspective. {% endhint %}

Depending on whether the provider allows parameterization and the types of parameterization allowed, you can customize the request to the provider's data source by specifying:
- Custom method
- Custom path
- Custom Request Body
- Custom Request Body Content Type

![Data-Source Parameterization](/docs/images/edc-ui-parameterization.png)

#### 2. Transfer Data to a Custom Datasink Config (JSON)

For more advanced data-sink endpoints not directly supported by the UI, you can enter your data-sink properties in JSON format.

![Custom Datasink Configuration](/docs/images/edc-ui-transfer-datasink.png)

**Example:**
{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "properties": {
    "type": "HttpData",
    "baseUrl": "https://webhook.site/86b9b7e6-eb27-4c5f-b7e5-336d5f157f15"
  }
}
```
{% endcode %}

Here, `"type": "HttpData"` indicates that the EDC will interpret the destination as a REST-API. The `baseUrl` is the endpoint receiving the data.

#### 3. Transfer Data via Custom Transfer Process Request (JSON)

For passing parameters to the providing Connector or detailed configuration of EDC transfer processes, use the Custom Transfer Process Request (JSON) option.

**Example:**
{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "protocol": "ids-multipart",
  "assetId": "this-will-be-overriden-by-the-ui",
  "contractId": "this-will-be-overriden-by-the-ui",
  "dataDestination": {
    "properties": {
      "type": "HttpData",
      "baseUrl": "https://my-data-sink/",
      "method": "POST"
    }
  },
  "properties": {
    "pathSegments": "12345/some-resource/6890",
    "queryParams": "a=b&c=d"
  },
  "transferType": {
    "contentType": "application/octet-stream",
    "isFinite": true
  },
  "managedResources": false,
  "connectorAddress": "this-will-be-overriden-by-the-ui--but-is-broken-right-now-as-said-above",
  "connectorId": "consumer"
}
```
{% endcode %}

Note that this option requires providing the data sink and may include fields for the transfer process request, if enabled by the provider. This is useful for specific cases, such as transferring files to blobs.

Click on ```Initiate Transfer``` to send the asset to the desired data sink.

{% hint style="warning" %} It is important to know that a data transfer can fail for a variety of reasons. This may be due to a misconfiguration of the data-source as well as the data-sink or unfulfilled contract policies set by the Provider, as contract policies are also evaluated at the time a transfer is initiated. {% endhint %}
 
