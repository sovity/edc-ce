## AWS S3 Transfer Documentation

### Introduction

The EDC provides the capability to transfer data from and to AWS S3 buckets. This guide provides step-by-step instructions on how to:
- Set up AWS S3 buckets using the AWS Console
- Prepare the necessary parameters for providing and consuming S3 buckets
- Configure the EDC to provide an S3 bucket as a data source
- Initiate a data transfer using a consuming EDC

### AWS Setup

To enable access to an AWS S3 bucket, follow these steps using the **AWS Console**.
This guide holds for AWS `long-term credentials`.

1. Create a `S3 Bucket`
  - Follow the AWS guide: [AWS S3 Create Bucket Documentation](https://docs.aws.amazon.com/AmazonS3/latest/userguide/create-bucket-overview.html)

1. Create an `IAM User`
   - Follow the AWS guide: [AWS IAM Create User Documentation](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_users_create.html)
   - Navigate to `IAM` > `Users` > `Add User`
   - Assign the necessary permissions to allow access to the S3 bucket
     - Reference: [AWS IAM Policies Documentation](https://docs.aws.amazon.com/IAM/latest/UserGuide/access_policies_managed-vs-inline.html)
     - Policies could be configured such that it is only possible to read/write certain objects in certain buckets

3. Create an `Access Key`
  - Follow the AWS guide: [AWS IAM Access Keys Documentation](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html)
   - Go to the IAM user details page
   - Select the `Security credentials` tab
   - Generate an `Access Key`: `Thirdparty-Service`

### Required Parameters

| Name                 | Description                                                                            |
|----------------------|----------------------------------------------------------------------------------------|
| `AWS_S3_REGION`      | The region that has been chosen for the S3 bucket during the `Create a S3 Bucket` step |
| `AWS_S3_BUCKET_NAME` | The name that has been chosen for the S3 bucket during the `Create a S3 Bucket` step   |
| `AWS_S3_OBJECT_NAME` | The object stored in the S3 bucket that needs to be provided/created                   |
| `AWS_KEY_ID`         | The keyId created in step `Create an Access Key`                                       |
| `AWS_SECRET`         | The secret of the key created in step `Create an Access Key`                           |

### EDC Provider

To configure an EDC instance as a data provider, follow these steps:
1. Refer to the general EDC data provisioning guide
2. Define `Policies` and `ContractDefinitions` (same as for other data sources)
3. Configure the `Asset` for AWS S3 with the EDC Management API using the following JSON payload:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "@context": {
    "edc": "https://w3id.org/edc/v0.0.1/ns/"
  },
  "properties": {
    "description": "description",
    "id": "{{ASSET_ID}}"
  },
  "dataAddress": {
    "@type": "DataAddress",
    "type": "AmazonS3",
    "region": "{{AWS_S3_REGION}}",
    "bucketName": "{{AWS_S3_BUCKET_NAME}}",
    "objectName": "{{AWS_S3_OBJECT_NAME}}",
    "accessKeyId": "{{AWS_KEY_ID}}",
    "secretAccessKey": "{{AWS_SECRET}}"
  }
}
```
{% endcode %}

### EDC Consumer

To consume an S3 bucket from another connector, a **Contract Agreement** must be successfully negotiated. This can be achieved using the EDC UI:
- Follow the guide for Finding Offers & Contracting in this documentation.
- The following variables are required for starting the `Transfer Process` later:
    - All of them can be seen by using the `Contracts` page of the EDC-Ui and viewing the details of the corresponding contract.
    - `COUNTER_PARTY_DSP` (Data Space Protocol endpoint of the provider)
    - `COUNTER_PARTY_BPN`  (Business Partner Number of the provider)
    - `ASSET_ID` (Identifier of the asset to be consumed)
    - `CONTRACT_AGREEMENT_ID` (Agreement ID of the negotiated contract)

**Initiating a Data Transfer**

Use the following JSON payload to start a data transfer request using the Transfer Process within the Management API.

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
  "@context": {
    "@vocab": "https://w3id.org/edc/v0.0.1/ns/"
  },
  "@type": "TransferRequest",
  "protocol": "dataspace-protocol-http",
  "counterPartyAddress": "{{COUNTER_PARTY_DSP}}",
  "connectorId": "{{COUNTER_PARTY_BPN}}",
  "assetId": "{{ASSET_ID}}",
  "dataDestination": {
    "@type": "https://w3id.org/edc/v0.0.1/ns/DataAddress",
    "https://w3id.org/edc/v0.0.1/ns/type": "AmazonS3",
    "https://w3id.org/edc/v0.0.1/ns/properties": {
      "region": "{{AWS_S3_REGION}}",
      "bucketName": "{{AWS_S3_BUCKET_NAME}}",
      "objectName": "{{AWS_S3_OBJECT_NAME}}",
      "accessKeyId": "{{AWS_KEY_ID}}",
      "secretAccessKey": "{{AWS_SECRET}}"
    }
  },
  "contractId": "{{CONTRACT_AGREEMENT_ID}}",
  "privateProperties": {},
  "transferType": "AmazonS3-PUSH"
}
```
{% endcode %}
