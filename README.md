# EDC Data Dashboard

**Please note: This repository does not contain production-grade code and is only intended for demonstration purposes.**

EDC Data Dashboard is a dev frontend application for [EDC Data Management API](https://github.com/eclipse-dataspaceconnector/DataSpaceConnector).

## Documentation

Developer documentation can be found under [docs/developer](docs/developer/), where the main concepts and decisions are captured as [decision records](docs/developer/decision-records/).

## Generate client code for EDC REST APIs

1. [optional] copy the current version of EDC's `openapi.yaml` file to `openapi/`. There is one checked in, so this is not required.
2. in a shell execute
   ```shell
   docker run --rm -v "${PWD}:/local" openapitools/openapi-generator-cli generate -i /local/openapi/openapi.yaml -g typescript-angular -o /local/src/modules/edc-dmgmt-client/
   ```
   This re-generates the service and model classes. 

> Please note that some of the client classes were edited manually after generation. When regenerating the classes for the API update be careful especially not to overwrite service `constructor` methods using the generator!

## Running the frontend locally
Should you want to run the frontend on your development machine, you'll have to configure some backend values. Those are stored in `app.config.json`, and 
by default contain the following:

```json
{
  "dataManagementApiUrl": "{{dataManagementApiUrl}}",
  "catalogUrl": "{{catalogUrl}}",
  "storageAccount": "{{account}}",
  "storageExplorerLinkTemplate": "storageexplorer://v=1&accountid=/subscriptions/{{subscriptionId}}/resourceGroups/{{resourceGroup}}/providers/Microsoft.Storage/storageAccounts/{{account}}&subscriptionid={{subscriptionId}}&resourcetype=Azure.BlobContainer&resourcename={{container}}",
}
```
Substitute the values as necessary:
- `apiKey`: enter here what your EDC instance expects in th `x-api-key` header
- `catalogUrl`: prepend your connector URL, e.g. `http://localhost`, assuming your catalog endpoint is exposed at port 8181, which is the default
- `dataManagementApiUrl`:  prepend your connector URL, e.g. `http://localhost`, assuming your IDS endpoint is exposed at port 9191
- `storageAccount`: insert the name of an Azure Blob Storage account to which the connector has access, otherwise data transfers won't work.

**Be extra careful NOT to commit those changes, as they might leak potentially sensitive information!!!**

As some extra safety consider running `git udpate-index --assume-unchanged src/assets/config/app.config.json` before changing this file.



## Deploy to Azure

Create a resource group and container registry:

```bash
export RESOURCE_GROUP=edc-data-dashboard
export ACR_NAME=edcdatadashboard
az group create --resource-group $RESOURCE_GROUP --location westeurope -o none
az acr create --resource-group $RESOURCE_GROUP --name $ACR_NAME --sku Standard --location westeurope --admin-enabled -o none
```

Dockerize the application and push it to the registry by running:

```bash
az acr build --registry $ACR_NAME --image edc-showcase/edc-data-dashboard:latest .
```

The docker image is now ready to be deployed to Azure Container Instances (ACI). The `app.config.json` file contains configuration which is fetched by the application at startup. This file can be overridden at deployment time by mounting a secret on `assets/config`. For each deployment you need to provide the corresponding connector backend URL, the storage account name and the API key using this secret. Deploy to ACI using the following command:

```bash
export CONNECTOR_DATA_URL=<CONNECTOR_DATA_URL>
export CONNECTOR_CATALOG_URL=<CONNECTOR_CATALOG_URL>
export STORAGE_ACCOUNT=<STORAGE_ACCOUNT>
export API_KEY=<API_KEY>

# deploy to ACI (when prompted for credentials use the username/password as available in Azure Portal: ACR->Access Keys)
az container create --image ${ACR_NAME}.azurecr.io/edc-showcase/edc-data-dashboard:latest \
--resource-group $RESOURCE_GROUP \
--name edc-data-dashboard \
--secrets "app.config.json"="{\"dataManagementApiUrl\": \"$CONNECTOR_DATA_URL\", \"catalogUrl\": \"$CONNECTOR_CATALOG_URL\", \"storageAccount\": \"$STORAGE_ACCOUNT\", \"apiKey\": \"$API_KEY\"}" \
--secrets-mount-path /usr/share/nginx/html/assets/config \
--dns-name-label edc-data-dashboard
```

## Contributing

See [how to contribute](https://github.com/eclipse-dataspaceconnector/DataSpaceConnector/blob/main/CONTRIBUTING.md) for details.
