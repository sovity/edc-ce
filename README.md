# EDC-Connector Broker Extension
To get started, a sample docker-compose file is located in the resources/docs folder.

## Run extension with EDC
### Configuration
#### Basic Configuration
- `EDC_IDS_TITLE`: _Title of the Connector_
- `EDC_IDS_DESCRIPTION`: _Description of the Connector_
- `EDC_IDS_ENDPOINT`: _URL of the Connectors endpoint_
- `EDC_IDS_CURATOR:` _URL of the curator, i.e. the company, which configures data offerings etc._
- `EDC_CONNECTOR_NAME:` _The name of the connector_
- `EDC_HOSTNAME:` _The host of the connector_
- `EDC_API_AUTH_KEY:` _The API authorization key of management API_
      
#### MDS Environment Configuration
The test environment is set by default.
- `EDC_BROKER_BASE_URL:` https://broker.dev.mobility-dataspace.eu
- `EDC_OAUTH_CLIENT_ID:` _To be able to start an EDC-Connector with the broker-extensions, the `SKI` and `AKI` of the connector certificate must be entered as `client-ID` in the docker-compose and the .jks must be placed under the path specified in the docker-compose (in the example in the folder `resources/vault/edc/`, see `EDC_KEYSTORE` setting)._
- `EDC_OAUTH_TOKEN_URL:` https://daps.dev.mobility-dataspace.eu/token
- `EDC_OAUTH_PROVIDER_JWKS_URL:` https://daps.dev.mobility-dataspace.eu/jwks.json

### Start
1. Login into GitHub Container Registry (GHCR): `$ docker login ghcr.io`.
2. Start via `$ docker compose up` in the docker-compose file folder

### Test extension
Use Postman (https://github.com/postmanlabs) and import collection located at `resources/docs/postman_collection.json`. Depending on your configuration changes, you need to adjust variables on collection `MDV > Variables > Current Value`
- `api_key` needs to be aligned with `EDC_API_AUTH_KEY`

To test Broker functionality, simply execute steps
1. `Publish Asset 1`
2. `Publish Policy 1`
3. `Publish ContractDefinition 1`: You will see a notification about registering resource at broker, which will then be reflected in the Broker's UI.
4. `Delete ContractDefinition 1`: You will see a notification about unregistering the resource at broker.

## FAQ 
### What should the client ID entry look like?
Example of a client-ID entry:

`EDC_OAUTH_CLIENT_ID: 7X:7Y:...:B2:94:keyid:6A:2B:...:28:80`

### How do you get the SKI and AKI of a p12 and how do you convert it to a jks?
There are two ways to generate the SKI/AKI and jks file.
You can use a script (if you're on WSL or Linux) or use KeyStore Explorer to manually generate them.

#### Option 1: With script
1. Open your bash console in the resources/docs directory
2. Run the script ``./get_client.sh [filepath].p12 [password]`` and substitute [filepath] to the certificate filepath and 
[password] to the certificate password
3. The jks file will be generated in the same folder as your p12 file and the SKI/AKI combination is printed out in the console

#### Option 2: Manually with KeyStore Explorer
Here we show the way via the tool `KeyStore Explorer` (https://github.com/kaikramer/keystore-explorer) using Windows, for direct commands see other examples online.

1. Convert the `p12` to a `jks`
- Open the `p12` in the KeyStore Explorer by entering the password
- `File` -> `Save as` -> Select `KeyStore Files` at `Files of Type` -> Directly save the file again as file of type `KeyStore Files` with a `.jks` extension in its name under `File Name`, e.g. `keystore.jks`
2. Get SKI and AKI
- Right-click on the KeyPair in KeyStore Explorer -> `Export` -> `Export Certificate Chain` -> Confirm the dialog with `Export` Button
- Double-click on the certificate under the specified storage path (Windows Certificate Manager opens)
- Under the `Details` section you will find the details about
    - `AKI` (Authority Key Identifier, dt.: Stellenschlüsselkennung) and
    - `SKI` (Subject Key Identifier, dt.: Schlüsselkennung des Antragstellers)
- Finally, the identifiers itself have to be manually combined into the required format `SKI:keyid:AKI` with upper case letters and colon separator (see the above example of the client-ID):

## License
This project is licensed under the Apache License 2.0 - see [here](LICENSE) for details.
