# AWS S3 / Azure Blob Transfer

This guide covers transferring data to and from cloud object storage. Use the tabs below to switch between **AWS S3** and **Azure Blob Storage** wherever the steps differ.

## Setup

{% tabs %}
{% tab title="AWS S3" %}
**AWS Setup:** To enable access to an AWS S3 bucket, follow these steps:

1. Create an `IAM User`
   - Navigate to `IAM` > `Users` > `Add User`
   - Assign the necessary permissions to allow access to the S3 bucket
   - A broad permission example: `AmazonS3FullAccess`

2. Create an `Access Key`
   - Go to the IAM user details page
   - Select the `Security credentials` tab
   - Generate an `Access Key`: `Thirdparty-Service`

**Requirements**
- Access to AWS S3 bucket with some file
- AWS S3 access key and secret key
{% endtab %}

{% tab title="Azure Blob" %}
**Azure Setup:** To enable access to an Azure Blob Storage container, follow these steps:

1. Open your `Storage Account`
   - Navigate to `portal.azure.com` > `Storage accounts`
   - Select the storage account that contains your blob data

2. Retrieve the `Primary Key`
   - In the left-hand menu, under `Security + networking`, select `Access keys`
   - You will see two keys: `key1` and `key2` (the Primary and Secondary keys)
   - Click `Show` next to `key1`, then copy the `Key` value (the Primary Key)

**Requirements**
- Access to Azure Blob Storage with some file
- Azure Blob secret (Primary Key or SAS Token)
{% endtab %}
{% endtabs %}

## Create an EDC Asset

The EDC asset is created using a `POST` request to the `/v3/assets` endpoint of the EDC Management API. Select your storage type below:

{% tabs %}
{% tab title="AWS S3" %}
{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
  },
  "@type": "Asset",
  "@id": "{{ASSET_ID}}",
  "dataAddress": {
    "@type": "DataAddress",
    "type": "AmazonS3",
    "region": "{{SOURCE_S3_BUCKET_REGION}}",
    "bucketName": "{{SOURCE_S3_BUCKET_NAME}}",
    "objectName": "{{PATH_TO_FILE_IN_SOURCE_S3_BUCKET}}",
    "accessKeyId": "{{AWS_ACCESS_KEY}}",
    "secretAccessKey": "{{AWS_SECRET_KEY}}"
  },
  "properties": {
    "http://purl.org/dc/terms/title": "{{ASSET_ID}}"
  }
}
```
{% endcode %}

{% hint style="info" %}
The `accessKeyId` and `secretAccessKey` are stored directly in the asset's data address. Anyone with read access to this asset can retrieve these credentials.
{% endhint %}

{% endtab %}

{% tab title="Azure Blob" %}
Unlike AWS S3, the Azure Blob credentials are not embedded in the asset. The asset's data address only references the secret by alias (the `keyName`), so the secret has to be stored in the vault **before** the asset can be served.

**Store the source secret in the vault**

Send a `POST` request to the `/v3/secrets` endpoint of the Management API with the following body, using your chosen `{{AZURE_VAULT_KEY_NAME}}` as the alias:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
  },
  "@type": "Secret",
  "@id": "{{AZURE_VAULT_KEY_NAME}}",
  "value": "{{SECRET_VALUE}}"
}
```
{% endcode %}

**Create the asset**

Send a `POST` request to the `/v3/assets` endpoint. The `keyName` must match the alias used above:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
  },
  "@type": "Asset",
  "@id": "{{ASSET_ID}}",
  "dataAddress": {
    "@type": "DataAddress",
    "type": "AzureStorage",
    "account": "{{AZURE_STORAGE_ACCOUNT}}",
    "container": "{{AZURE_CONTAINER}}",
    "blobName": "{{AZURE_BLOB_NAME}}",
    "keyName": "{{AZURE_VAULT_KEY_NAME}}"
  },
  "properties": {
    "http://purl.org/dc/terms/title": "{{ASSET_ID}}"
  }
}
```
{% endcode %}

{% endtab %}
{% endtabs %}

## Publish an EDC Data Offer

Once the asset is created, you can publish a data offer with this asset either using the sovity EDC UI or the Management API. The following steps walk you through the EDC UI:

Go to the EDC UI, navigate to "Data Offers" and click "Publish Data Offer".

![Data Offer Page](images/PublishDataOffer.png)

Publish the Data Offer by selecting the newly created asset and the correct policies.

![Publish Data Offer Page](images/PublishDataOfferDetails.png)


## Consuming a data offer

### Requirements
- DSP endpoint of counterparty connector
- Participant ID (BPN) of counterparty connector
- Asset ID of the asset
- Access to your EDC's Management API


### Step 1: Get Contract Agreement ID

Before starting a transfer, you need to successfully negotiate a contract for a data offer. This will give you a Contract Agreement ID.

You can do this either [using the sovity EDC-UI](#step-1a-use-edc-ui) or [using the Management API](#step-1b-use-management-api).

#### Step 1a: Use EDC-UI

Go to the sovity EDC-UI of your EDC.

Navigate to the Catalog Browser and put in the DSP-Endpoint and Participant ID (BPN) of the counterparty EDC:

![Catalog Browser](images/CatalogBrowser.png)

Find the data offer and negotiate a contract. Follow the steps until the contract is successfully negotiated:

![Initiate Contract Negotiation](images/NegotiateContract.png)

You should get redirected to the Contract Details page, where the Contract Agreement ID will be displayed:

![Contract Details Page](images/ContractDetails.png)


#### Step 1b: Use Management API
##### Request Catalog
- Send `POST` request to the `/v3/catalog/request` endpoint of the Management API with the following body:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
       "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "@type": "CatalogRequest",
    "counterPartyAddress": "{{COUNTERPARTY_DSP}}",
    "counterPartyId": "{{COUNTERPARTY_BPN}}",
    "protocol": "dataspace-protocol-http",
    "querySpec": {
        "filterExpression": {
            "operandLeft": "id",
            "operator": "=",
            "operandRight": "{{ASSET_ID}}"
        }
    }
}
```
{% endcode %}

- Copy the Data Offer ID from the `odrl:hasPolicy` -> `@id` field to the placeholder `OFFER_ID` in the next request
  - It should look something like this: `dGVzdC1kZg==:dGVzdC1kZg==:MDE5ZGRlZjItZmVlNy03MDExLTgzZDAtZTY1Nzk1MDQwNDQw`
- Copy the values for the `odrl:permission`, `odrl:prohibition` and `odrl:obligation` fields to the next request


##### Negotiate a contract
- Send `POST` request to the `/v3/contractnegotiations` endpoint of the Management API with the following body:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "@type": "ContractRequest",
    "counterPartyAddress": "{{COUNTERPARTY_DSP}}",
    "protocol": "dataspace-protocol-http",
    "policy": {
        "@context": "http://www.w3.org/ns/odrl.jsonld",
        "@type": "odrl:Offer",
        "@id": "{{OFFER_ID}}",
        "assigner": "{{COUNTERPARTY_BPN}}",
        "target": "{{ASSET_ID}}",
        "odrl:permission": ["<<Fill with copied value>>"],
        "odrl:prohibition": ["<<Fill with copied value>>"],
        "odrl:obligation": ["<<Fill with copied value>>"]
    }
}
```
{% endcode %}

##### Check if negotiation was successful
- Send `GET` request to the `/v3/contractnegotiations/{{CONTRACT_NEGOTIATION_ID}}/state` endpoint of the Management API
- If the state is `TERMINATED`, the negotiation failed
- If the state is `FINALIZED`, the negotiation was successful


##### Get Contract Agreement ID
- Send `GET` request to the `/v3/contractnegotiations/{{CONTRACT_NEGOTIATION_ID}}/agreement` endpoint of the Management API
- Extract Contract Agreement ID from `@id` field in response


### Step 2: Start the transfer

{% tabs %}
{% tab title="AWS S3" %}
Send a `POST` request to the `/v3/transferprocesses` endpoint of the Management API with the following body:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "@type": "TransferRequest",
    "protocol": "dataspace-protocol-http",
    "contractId": "{{CONTRACT_AGREEMENT_ID}}",
    "counterPartyAddress": "{{COUNTERPARTY_DSP}}",
    "dataDestination": {
        "@type": "DataAddress",
        "type": "AmazonS3",
        "properties": {
            "region": "{{DESTINATION_S3_BUCKET_REGION}}",
            "bucketName": "{{DESTINATION_S3_BUCKET_NAME}}",
            "objectName": "{{PATH_TO_FILE_IN_DESTINATION_S3_BUCKET}}",
            "accessKeyId": "{{AWS_ACCESS_KEY}}",
            "secretAccessKey": "{{AWS_SECRET_KEY}}"
        }
    },
    "transferType": "AmazonS3-PUSH"
}
```
{% endcode %}

{% endtab %}

{% tab title="Azure Blob" %}
**Step 2a: Store the Azure Blob secret key in the vault**

Unlike AWS S3, the Azure Blob credentials are not passed inline in the request. Instead, the secret key is stored in the connector's vault and the data address only references it by alias (the `keyName`). This means the secret has to be stored in the vault before the transfer can succeed.

Send a `POST` request to the `/v3/secrets` endpoint of the Management API with the following body:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "@type": "Secret",
    "@id": "{{AZURE_STORAGE_ACCOUNT}}-key1",
    "value": "{{SECRET_VALUE}}"
}
```
{% endcode %}

{% hint style="info" %}
The secret key alias needs to be the Azure Storage Account name suffixed with `-key1`.
{% endhint %}

**Step 2b: Start the transfer**

Send a `POST` request to the `/v3/transferprocesses` endpoint of the Management API with the following body:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
  },
  "@type": "TransferRequest",
  "protocol": "dataspace-protocol-http",
  "contractId": "{{CONTRACT_AGREEMENT_ID}}",
  "counterPartyAddress": "{{COUNTERPARTY_DSP}}",
  "dataDestination": {
    "@type": "DataAddress",
    "type": "AzureStorage",
    "properties": {
      "account": "{{AZURE_STORAGE_ACCOUNT}}",
      "container": "{{AZURE_CONTAINER}}",
      "folderName": "{{AZURE_DESTINATION_FOLDER_NAME}}",
      "blobName": "{{AZURE_BLOB_NAME}}"
    }
  },
  "transferType": "AzureStorage-PUSH"
}
```
{% endcode %}

{% hint style="info" %}
The `dataDestination.properties.keyName` field in the request body (used in the [Azure Blob asset creation request](#create-an-edc-asset)) is ignored here. It is hard-coded to your Azure Storage Account name suffixed with `-key1`. So for the Azure Blob transfer to work, store your secret key in the vault under your Azure Storage Account name suffixed with `-key1` (for example, `mystorageaccount-key1`).
{% endhint %}

{% endtab %}
{% endtabs %}
