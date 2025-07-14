# Data Transfer Process

{% hint style="warning" %} When using the Connector UI, only a HttpData-Push can be triggered, not an HttpData-Pull. {% endhint %}

To transfer the data to your desired data sink, navigate to the `Contracts` page. This page displays all your contract agreements, including both consuming and providing contract agreements. You can easily distinguish between them by the arrow on the left side on each row next to the `Contract` column (Up = providing, Down = consuming). You can inspect details of your contract agreements, such as the counterpart Connector.

## Initiating a Data Transfer (HttpData-Push)

1. **Selecting a Contract Agreement**
   - Click on a consuming contract agreement to open a detail view displaying details of the contract agreement.
  
![Contract Agreement](/docs/images/consumer-contracts-overview-1.png)

2. **Defining Data Sink Properties**
   - Click on `Transfer` to define your data sink properties.

![Initiating data transfer](/docs/images/consumer-contracts-transfer-1.png)

### Supported Data Sink Types

There are two data sink types supported:

1. `Custom Datasink Config (JSON)`
2. `REST-API Endpoint`

![Datasink properties](/docs/images/consumer-contracts-transfer-type-1.png)

#### 1. Transfer to a REST-API Endpoint

To transfer data to a REST-API endpoint, select an HTTP method and provide the URL of the data sink. You can also add additional headers, such as for authentication.

#### 2. Transfer Data to a Custom Datasink Config (JSON)

For more advanced data-sink endpoints not directly supported by the UI, you can enter your data-sink properties in JSON format.

![Custom Datasink Configuration](/docs/images/consumer-contracts-transfer-type-custom-1.png)

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

Click on `Initiate Transfer` to send the asset to the desired data sink.

{% hint style="warning" %} It is important to know that a data transfer can fail for a variety of reasons. This may be due to a misconfiguration of the data-source as well as the data-sink or unfulfilled contract policies set by the Provider, as contract policies are also evaluated at the time a transfer is initiated. {% endhint %}
 
