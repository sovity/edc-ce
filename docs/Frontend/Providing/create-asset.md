# 2. Create an Asset

The first step in providing data with the Eclipse Dataspace Components-Connector (EDC Connector) is creating an asset. Start by clicking on the tab `Assets` followed by a click on `New Asset` on the top right.

![Create Asset](/docs/images/provider-asset-overview-1.png)

You will be directed to the `Create New Asset` flow dialog, allowing you to define all relevant data for the asset itself and other necessary data for the data source, if the data and its data source is already available. Two offer types are available for this purpose to be selected, depending on whether a data source already exists or whether it is acted only upon request.

![Create Asset Dialog](/docs/images/provider-asset-create-1.png)

First, it must be decided whether the new data offer has a data source that is already available or whether the data will be provided on request. Depending on your selection, different fields will be available in the following dialog. For existing data sources, the relevant data must be entered, from the type of data source to the method to be used upon transfer start, the URL, and other necessary mechanisms related to the data source.


| Field Name          | Field Type | Example Value                                    | Description |
|---------------------|------------|--------------------------------------------------|-------------|
| Title                | String     | Bitcoin Data                                     | This title will also be the name of the data offering displayed in the catalog. |
| Assed ID            | URN        | urn:artifact:bitcoindata:1.0                     | ID will automatically be generated and will follow a URL-format and be prefixed by urn:artifact: |
| Description         | String     | Current Bitcoin Status                           | Provide a more detailed description of the asset content. |
| Keywords            | String     | Cryptocurrency, Bitcoin                          | Keywords make the data offer easier to find. |
| Version             | String     | 1.0                                              | Information on the version of the data |
| Language            | String     | English                                          | Language of the content |
| Content Type        | MIME       | application/json                                 | Describes the content type of the data as a MIME type, see [MIME Types](https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types) |
| Endpoint Documentation | URL    | https://docs.coincap.io/                         | Link to the technical documentation about the data to be received. |
| Publisher           | URL        | https://coincap.io/                              | URL of the original publisher of the data. |
| Standard License    | URL        | https://creativecommons.org/licenses/by/4.0/deed.en | Link to the license under which the data are offered |

You can connect data from any REST-API endpoint by supplying a HTTP-method and the endpoint-URL. Depending on your data sources, further settings may be required, such as necessary headers for authentication in the event of a data transfer.

#### Authentication
By using the `Authentication` dropdown, you could for example enable basic HTTP-auth or bearer-token authentication in the header towards the data source by selecting `Header with Value` in the dropdown. Alternatively, with the selection `Header with Vault Secret`, this header can have its value supplied by a vault-secret in our vault connected to your CaaS.

**Type: Custom Datasource**  
If the UI does not support your desired data address type, you can provide a custom data address config JSON.

![Asset with Custom Datasource](/docs/images/provider-asset-custom-datasource-1.png)

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

Click on `Create` to create the data offer.
