# 2. Create an Asset

The first step in providing data with the Eclipse Dataspace Components-Connector (EDC Connector) is creating an asset. Start by clicking on the tab ```Assets``` followed by a click on ```Create asset``` on the top right.

![Create Asset](/docs/images/edc-ui-create-asset.png)

A pop-up window will appear, allowing you to define general metadata for the asset.

![Create Asset Dialog](/docs/images/edc-ui-create-asset-dialog.png)

| Field Name          | Field Type | Example Value                                    | Description |
|---------------------|------------|--------------------------------------------------|-------------|
| Name                | String     | Bitcoin Data                                     | This name will also be the name of the data offering displayed in the catalog. |
| Version             | String     | 1.0                                              | Information on the version of the data |
| Assed ID            | URN        | urn:artifact:bitcoindata:1.0                     | ID will automatically be generated and will follow a URL-format and be prefixed by urn:artifact: |
| Description         | String     | Current Bitcoin Status                           | Provide a more detailed description of the asset content. |
| Keywords            | String     | Cryptocurrency, Bitcoin                          | Keywords make the data offer easier to find. |
| Language            | String     | English                                          | Language of the content |
| Content Type        | MIME       | application/json                                 | Describes the content type of the data as a MIME type, see [MIME Types](https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types) |
| Endpoint Documentation | URL    | https://docs.coincap.io/                         | Link to the technical documentation about the data to be received. |
| Publisher           | URL        | https://coincap.io/                              | URL of the original publisher of the data. |
| Standard License    | URL        | https://creativecommons.org/licenses/by/4.0/deed.en | Link to the license under which the data are offered |

**Connectors in Mobility Data Space**
Proceed by clicking on ```Advanced Information```.
The MDS has specific additional properties relevant to it. For example, a data category can be selected.
This area is not visible for other connector variants.

**All Connectors**
Proceed by clicking on ```Datasource Information```. In the next step, you will connect your data-source to the asset and optionally add additional headers as well as links to important files, such as documentation and license.

### Use a REST-API Endpoint as Datasource
You can connect data from any REST-API endpoint by supplying a HTTP-method and the endpoint-URL. Depending on your data sources, further settings may be required, such as necessary headers for authentication in the event of a data transfer.

![Asset Datasource Information](/docs/images/edc-ui-create-asset-datasource.png)

#### Parameterization
By enabling parameterization, you can offer data consumers, to configure parameters of your data source in an event of a data-transfer. Be aware, that basic knowledge about your data-source is necessary for the consumer, to use the parameterization. For example, data can be requested by the consumer for a specific use case by specifying certain settings via query parameters of the data-source that are forwarded directly to the provider's data source.

**Available parameterizations**
- **Method Parameterization**: You can allow your consumer to define the method (e.g. GET, POST, etc.) on how you fetch the data from your data source.
- **Path Parameterization**: You can allow your consumer to define additional path segments of your data source on a data transfer request. If you have your data URL https://api.coincap.io/v2/assets and allow fetching specific data by adding "/{coin_name}", you could for example allow consumers to fetch data from https://api.coincap.io/v2/assets/bitcoin by enabling this option.
- **Query Param Parameterization**: You can allow your consumer to define the query parameters on a data transfer request. Thus, consumers could define query-parameters of the data-source themselves. 
- **Request Body Parameterization**: On methods like POST or PUT, HTTP allows to send additional data in the body of the request to your data-source. For example if the pre-selected method is POST and the endpoint allows to add data in the request-body, the consumer could send it's data within a data transfer request which is then used to send towards the data-source as body.

#### Authentication
By specifying the header "Authorization", you could for example enable basic HTTP-auth or bearer-token authentication. Optionally a single header can be marked as authentication header and can have its value supplied by a vault-secret in our vault connected to your CaaS, "Header with Vault Secret". 

**Custom Datasource**  
If the UI does not support your desired data address type, you can provide a custom data address config JSON.

![Asset with Custom Datasource](/docs/images/edc-ui-create-asset-datasource-custom.png)

Example:

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

*Note: Currently, only one endpoint can be connected unless the credentials for the APIs are exactly the same.*

Proceed by providing metadata as explained above.

Click on ```Create``` to create the asset.

You have now connected a data source to the Connector. To share this data with other data space participants, you need to define a Policy and then create a Contract Definition.
 
