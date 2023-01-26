<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ui">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extensions</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues">Request Feature</a>
  </p>
</div>

## About The Project
In this repo are different extensions of the EDC-Connector, among others a Broker-Extension to communicate with an IDS-Broker as well as a ClearingHouse-Extension to communicate with an IDS-ClearingHouse.

## Getting Started

To get started and run the extensions with an EDC, a sample docker-compose file is located in the root folder.

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
The dev environment is set by default.
- `EDC_BROKER_BASE_URL:` https://broker.dev.mobility-dataspace.eu
- `EDC_CLEARINGHOUSE_LOG_URL`: https://clearing.dev.mobility-dataspace.eu/messages/log
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

### Additional Meta Information
The Broker Extension supports the following additional meta information to be sent to the broker:
```
- id: "asset:prop:id"
- name: "asset:prop:name"
- contentType: "asset:prop:contenttype"
- description: "asset:prop:description"
- version: "asset:prop:version"
- keywords: "asset:prop:keywords"
- language: 'asset:prop:language'
- publisher: "asset:prop:publisher"
- standardLicense: "asset:prop:standardLicense"
- endpointDocumentation: "asset:prop:endpointDocumentation"

MDS-specific properties:
- dataCategory: 'http://w3id.org/mds#dataCategory'
- dataSubcategory: 'http://w3id.org/mds#dataSubcategory'
- dataModel: 'http://w3id.org/mds#dataModel'
- geoReferenceMethod: 'http://w3id.org/mds#geoReferenceMethod'
- transportMode: 'http://w3id.org/mds#transportMode'
```

For an example of how the API calls are to be made, see the postman collection, where examples of the properties are
also shown.
## FAQ 
### What should the client ID entry look like?
Example of a client-ID entry:

`EDC_OAUTH_CLIENT_ID: 7X:7Y:...:B2:94:keyid:6A:2B:...:28:80`

### How do you get the SKI and AKI of a p12 and how do you convert it to a jks?
You can use a script (if you're on WSL or Linux) to generate the SKI, AKI and jks file.

0. Make sure you're on Linux or on a bash console (e.g. WSL or Git Bash) and have openssl and keytool installed
1. Navigate in the console to the resources/docs directory
2. Run the script ``./get_client.sh [filepath].p12 [password]`` and substitute [filepath] to the p12 certificate filepath and 
[password] to the certificate password
3. The jks file will be generated in the same folder as your p12 file and the SKI/AKI combination is printed out in the console.
Copy the SKI:AKI combination and use it to start the EDC (optionally also save it to your password manager).

## License
This project is licensed under the Apache License 2.0 - see [here](LICENSE) for details.

## Contact
Sovity GmbH - contact@sovity.de 
