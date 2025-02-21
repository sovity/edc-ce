## AWS S3 Transfer Documentation

**AWS Setup:** To enable access to an AWS S3 bucket, follow these steps:

1. Create an `IAM User`
   - Navigate to `IAM` > `Users` > `Add User`
   - Assign the necessary permissions to allow access to the S3 bucket
   - A broad permission example: `AmazonS3FullAccess`

2. Create an `Access Key`
   - Go to the IAM user details page
   - Select the `Security credentials` tab
   - Generate an `Access Key`: `Thirdparty-Service`

**EDC Asset with AWS S3 datasource**: The EDC asset can be created using the EDC Management-API with the following payload:

{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "edc": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "asset": {
        "properties": {
            "asset:prop:name": "aws s3",
            "asset:prop:description": "Asset capable of accessing s3 storage",
            "edc:id": "aws-s3"
        }
    },
    "dataAddress": {
        "type": "AmazonS3",
        "region": "eu-central-1",
        "bucketName": "e9a9ae45-0f27-472e-817b-f54eee85e7d1",
        "keyName": "aws-s3-test.txt",
        "accessKeyId": "{{AWS_KEY_ID}}",
        "secretAccessKey": "{{AWS_SECRET}}"
    }
}
```
{% endcode %}
