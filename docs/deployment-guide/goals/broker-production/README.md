# Broker Productive Deployment Guide

## About this Guide

This is a productive deployment guide for self-hosting a functional sovity Broker.

## Deployment Units

| Deployment Unit                                                | Version / Details                                                                       |
|----------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| Reverse Proxy that merges the UI+Backend and removes the ports | (deployment specific)                                                                   |
| Postgresql                                                     | 15 or compatible version                                                                |
| Broker Backend                                                 | broker-server-ce, see [CHANGELOG.md](../../../../CHANGELOG.md) for compatible versions. |
| Broker UI                                                      | edc-ui, see  [CHANGELOG.md](../../../../CHANGELOG.md) for compatible versions.                      |

### Configuration

There is a [docker-compose.yaml](../../../../docker-compose.yaml) to try out the broker locally. 
However, a productive release will require a few more configuration options,
so you should only use it to check if the released version is roughly working or if it's broken.

#### Reverse Proxy Configuration

- The broker is meant to be served via TLS/HTTPS.
- The broker is meant to be deployed with a reverse proxy merging the following ports:
  - The UI's `8080` port.
  - The Backend's `11002` port.
  - The Backend's `11003` port.
- The mapping should look like this:
  - `https://[MY_EDC_FQDN]/backend/api/dsp` -> `broker-backend:11003/backend/api/dsp`
  - `https://[MY_EDC_FQDN]/backend/api/management` -> `broker-backend:11002/backend/api/management`
  - All other requests -> `broker-ui:8080`

#### Backend Configuration

A productive configuration will require you to join a DAPS.

For that you will need a SKI/AKI ClientID. Please refer
to [edc-extension's Getting Started Guide](https://github.com/sovity/edc-extensions/tree/main/docs/getting-started#faq)
on how to generate one.

The DAPS needs to contain the claim `referringConnector=broker` for the broker.
The expected value `broker` could be overridden by specifying a different value for `MY_EDC_PARTICIPANT_ID`.

```yaml
# Required: Fully Qualified Domain Name
MY_EDC_FQDN: "example.com"

# Required: DB
MY_EDC_JDBC_URL: jdbc:postgresql://broker-postgresql:5432/edc
MY_EDC_JDBC_USER: edc
MY_EDC_JDBC_PASSWORD: edc

# Required: List of EDCs to fetch
EDC_BROKER_SERVER_KNOWN_CONNECTORS: "https://connector-a/api/dsp,https://connector-b/api/dsp"

# List of Data Space Names for special Connectors (default: '')
EDC_BROKER_SERVER_KNOWN_DATASPACE_CONNECTORS: "Mobilithek=https://some-connector/api/dsp,OtherDataspace=https://some-other-connector/api/dsp"

# Required: DAPS credentials
EDC_OAUTH_TOKEN_URL: 'https://daps.test.mobility-dataspace.eu/token'
EDC_OAUTH_PROVIDER_JWKS_URL: 'https://daps.test.mobility-dataspace.eu/jwks.json'
EDC_OAUTH_CLIENT_ID: '_your SKI/AKI_'
EDC_KEYSTORE: '_your keystore file_' # Needs to be available as file in the running container
EDC_KEYSTORE_PASSWORD: '_your keystore password_'
EDC_OAUTH_CERTIFICATE_ALIAS: 1
EDC_OAUTH_PRIVATE_KEY_ALIAS: 1

# Required: Management API Key
EDC_API_AUTH_KEY: "ApiKeyDefaultValue"

# Required: Admin Api Key
EDC_BROKER_SERVER_ADMIN_API_KEY: DefaultBrokerServerAdminApiKey
```

All pre-configured config values for either the broker server or the underlying EDC can be found
in [launcher/.env.broker](../../../../connector/launchers/.env.broker).

#### UI Configuration

```yaml
# Required: Profile
EDC_UI_ACTIVE_PROFILE: broker

# Required: Management API URL
EDC_UI_MANAGEMENT_API_URL: https://my-broker.com/backend/api/management

# Required: Management API Key
EDC_UI_MANAGEMENT_API_KEY: "ApiKeyDefaultValue"
```

#### Adding Connectors at runtime

Connectors can be dynamically added at runtime by using the following endpoint:

```shell script
# Response should be 204 No Content
curl --request PUT \
  --url 'http://localhost:11002/backend/api/management/wrapper/broker/connectors?adminApiKey=DefaultBrokerServerAdminApiKey' \
  --header 'Content-Type: application/json' \
  --header 'x-api-key: ApiKeyDefaultValue' \
  --data '["https://some-new-connector/api/dsp", "https://some-other-new-connector/api/dsp"]'
```

#### Removing Connectors at runtime

Connectors can be dynamically removed at runtime by using the following endpoint:

```shell script
# Response should be 204 No Content
curl --request DELETE \
  --url 'http://localhost:11002/backend/api/management/wrapper/broker/connectors?adminApiKey=DefaultBrokerServerAdminApiKey' \
  --header 'Content-Type: application/json' \
  --header 'x-api-key: ApiKeyDefaultValue' \
  --data '["https://some-connector-to-be-removed/api/dsp", "https://some-other-connector-to-be-removed/api/dsp"]'
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## License

Distributed under the Apache 2.0 License. See `LICENSE` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contact

contact@sovity.de

<p align="right">(<a href="#readme-top">back to top</a>)</p>
