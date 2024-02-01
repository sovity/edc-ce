<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">sovity Community Edition EDC:<br />Getting Started</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## Getting Started

The following example shows you how to fully configure an MDS EDC.

1. Ensure the [EDC Image](../../connector) you are using in the [docker-compose.yaml](../../docker-compose.yaml) file
   is `edc-ce-mds`.
2. Adjust the Connector Metadata:
   ```yaml
   # Connector Technical Name
   MY_EDC_NAME_KEBAB_CASE: "example-connector"

   # Connector Localized Name / Title
   MY_EDC_TITLE: "EDC Connector"

   # Connector Description Text
   MY_EDC_DESCRIPTION: "sovity Community Edition EDC Connector"

   # Connector Curator
   # The company using the EDC Connector, configuring data offers, etc.
   MY_EDC_CURATOR_URL: "https://example.com"
   MY_EDC_CURATOR_NAME: "Example GmbH"

   # Connector Maintainer
   # The company hosting the EDC Connector
   MY_EDC_MAINTAINER_URL: "https://sovity.de"
   MY_EDC_MAINTAINER_NAME: "sovity GmbH"
   ```
3. Generate your `.jks` file and your SKI/AKI ClientID. See [FAQ](#faq) for more information.
4. Adjust your data space specific configuration:
    ```yaml
    # Required by the broker-extensions,
    EDC_OAUTH_CLIENT_ID: '_your SKI/AKI_'
    EDC_KEYSTORE: '_your keystore file relative to docs/getting-started/secrets_'
    EDC_KEYSTORE_PASSWORD: '_your keystore password_'

    # MDS Test Environment
    EDC_OAUTH_TOKEN_URL: 'https://daps.test.mobility-dataspace.eu/token'
    EDC_OAUTH_PROVIDER_JWKS_URL: 'https://daps.test.mobility-dataspace.eu/jwks.json'
    EDC_BROKER_BASE_URL: 'https://broker.test.mobility-dataspace.eu'
    EDC_CLEARINGHOUSE_LOG_URL: 'https://clearing.test.mobility-dataspace.eu/messages/log'
    ```
5. Start the EDC
   ```shell
   # Log-in into the Github Container Registry
   docker login ghcr.io

   # Fetches latest images
   docker compose pull

   # Runs EDC and EDC UI at localhost:11000
   docker compose up
   ```
6. Visit [localhost:11000](http://localhost:11000)

## Security

Since an API Key must be set for the Management-API in the docker-compose file and is considered to be a security relevant setting, here are a few tips on what should be taken into account when setting a value in the docker-compose or the configuration of the infrastructure, when deploying it in a productive environment:
- Restrict access to the Management-API to your internal network
- Use an API Key with high entropy (length, complexity) when configuring the value (e.g. [a-zA-Z0-9+special chars]{32+ chars}).
- Limit the header size in the reverse proxy so that only a certain number of API Keys can be tested with one API-request (e.g. limit to 8kb).
- Limit the access rate to the API endpoints and monitor access for attacks like brute force attacks.

If configured insufficiently, this security relevant Management-API could be exploited to attack the EDC's database, thereby attacking the surrounding network.

## OpenAPI Yaml

There's an [openapi.yaml](../openapi.yaml) that includes the vanilla EDC endpoints exposed by the current control- and
data plane APIs.

## Postman Collection

To further try out our EDC Backend without the UI, try our Postman Collection:

1. Use Postman (https://github.com/postmanlabs)
2. Import the collection [docs/postman_collection.json](../postman_collection.json).
3. Depending on your configuration changes, you need to adjust variables on collection `MDV > Variables > Current Value`
4. `api_key` needs to be aligned with `EDC_API_AUTH_KEY`
5. To test the MDS Broker functionality, simply execute steps
    1. `1 Create Asset`
    2. `2 Create Policy`
    3. `3 Create ContractDefinition`: You will see a notification about registering resource at broker, which will then
       be reflected in the Broker's UI.
    4. `Delete ContractDefinition`: You will see a notification about unregistering the resource at broker.

## Docker Compose File Variants

There are currently two maintained Docker Compose files in our root directory.

| File                                                     | Description                                             |
|----------------------------------------------------------|---------------------------------------------------------|
| [docker-compose.yaml](../../docker-compose.yaml)         | Run a stable version.                                   |
| [docker-compose-dev.yaml](../../docker-compose-dev.yaml) | Run the current development version, which might break. |

## FAQ
### What should the client ID entry look like?
Example of a client-ID entry:

`EDC_OAUTH_CLIENT_ID: 7X:7Y:...:B2:94:keyid:6A:2B:...:28:80`

### How do you get the SKI and AKI of a p12 and how do you convert it to a jks?
You can use a script (if you're on WSL or Linux) to generate the SKI, AKI and jks file.

1. Make sure you're on Linux or on a bash console (e.g. WSL or Git Bash) and have openssl and keytool installed
2. Navigate in the console to the resources/docs directory
3. Run the script `./secrets/generate_ski_aki.sh [filepath].p12 [password]` and substitute [filepath] to the p12 certificate filepath and
   [password] to the certificate password
4. The jks file will be generated in the same folder as your p12 file and the SKI/AKI combination is printed out in the console.
   Copy the SKI:AKI combination and use it to start the EDC (optionally also save it to your password manager).

### Where should the connector certificate be stored?
In the default configuration the connector certificate should be stored inside a `keystore.jks` in a folder `docs/getting-started/secrets` next to the docker-compose. The path and keystore name can be edited in the env-variable `EDC_KEYSTORE`.

### Can I run a connector locally and consume data from an online connector?
No, locally run connectors cannot exchange data with online connectors. A connector must have a proper URL + configuration and be accesible from the data provider via REST calls.

### Can I disable the Broker- and/or ClearingHouse-Client-Extensions dynamically?
Yes, if the two extensions are included, they can still be disabled via properties.
The default settings can be found in `docker-compose.yaml` and can be changed there.
````java
# Extension Configuration
BROKER_CLIENT_EXTENSION_ENABLED: true
CLEARINGHOUSE_CLIENT_EXTENSION_ENABLED: true
````

## Further Documentation
- [Manging a sovity EDC Connector via the API Wrapper Java Client Library](../getting-started/documentation/api_wrapper.md)
- [How to share parameterized HTTP data sources to expose entire APIs](../getting-started/documentation/parameterized_assets.md)
- [How Can a Pull-Data-Transfer Be Performed?](../getting-started/documentation/pull-data-transfer.md)
