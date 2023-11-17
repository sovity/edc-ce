<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->

<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
<a href="https://github.com/sovity/edc-broker-server-extension">
<img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
</a>

<h3 align="center">Broker Server</h3>
<p align="center" style="padding-bottom:16px">
Broker Backend &amp; EDC Extensions.
<br />
<a href="https://github.com/sovity/edc-broker-server-extension/issues/new?template=bug_report.md">Report Bug</a>
·
<a href="https://github.com/sovity/edc-broker-server-extension/issues/new?template=feature_request.md">Request Feature</a>
</p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
   <summary>Table of Contents</summary>
   <ol>
      <li><a href="#about-the-project">About The Project</a></li>
      <li><a href="#development">Development</a></li>
      <li><a href="#releasing">Releasing</a></li>
      <li><a href="#deployment">Deployment</a></li>
      <li><a href="#license">License</a></li>
      <li><a href="#contact">Contact</a></li>
   </ol>
</details>

## About The Project

[Eclipse Dataspace Components](https://github.com/eclipse-edc) (EDC) is a framework
for building dataspaces, exchanging data securely with ensured data sovereignty.

[sovity](https://sovity.de/) extends the EDC Connector's functionality with extensions to offer
enterprise-ready managed services like "Connector-as-a-Service", out-of-the-box fully configured DAPS
and integrations to existing other dataspace technologies.

An IDS Broker is a central component of a dataspace that operates on the IDS protocol, that aggregates and indexes
connectors and data offers.

This IDS Broker is written on basis of the EDC and should be used in tandem with the Broker UI.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Development

### Local Development

#### Local Backend Development

For local backend development, access to the GitHub Maven Registry is required.

To access the GitHub Maven Registry you need to provide the following properties, e.g. by providing
a `~/.gradle/gradle.properties`.

```properties
gpr.user={your github username}
gpr.key={your github pat with packages.read}
```

Developing the Broker Backend tests are used to validate functionality:

- There are Integration Tests using the Broker Server Java Client Library for testing API Endpoints of a running
  backend.
- There are Integration Tests using the Broker Server Java Client Library and sovity EDC Extensions to integration
  test the Broker with a running EDC where communication works through the Data Space Protocol (DSP).
- There are Unit Tests with Mockito for testing local complexity, e.g. mappers, data structures, utilities.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

#### Local UI Development

The Broker UI is a profile `broker` of the [EDC UI](https://github.com/sovity/edc-ui):

The Broker UI depends on the NPM
Package [@sovity/broker-server-client](https://www.npmjs.com/package/@sovity.de/broker-server-client) built on the main
branch or on releases.

Local Broker UI Development can start with the type-safe broker server fake backend once the Client Library version is
bumped to contain the up-to-date API Models.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Local E2E Development

There is currently no support for Local E2E Development (a locally running backend build server and a locally running
frontend build server).

For debugging UI issues, however, the UI can be manually configured to use a live backend, e.g. one started via
the [docker-compose.yaml](#local-demo).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Local Demo

There is a [docker-compose.yaml](docker-compose.yaml) that starts a broker and a connector.

At release time it is pinned down to the release versions.

Mid-development it might be un-pinned back to latest versions.

|                     | Broker                                                           | Conncetor                                                                    |
|---------------------|------------------------------------------------------------------|:-----------------------------------------------------------------------------|
| Homepage            | http://localhost:11000                                           | http://localhost:22000                                                       |
| Management Endpoint | http://localhost:11002/api/management                            | http://localhost:22002/api/management                                        |
| Management API Key  | `ApiKeyDefaultValue`                                             | `ApiKeyDefaultValue`                                                         |
| Connector Endpoint  | http://broker:11003/api/dsp <br> Requires Docker Compose Network | http://connector:22003/api/dsp          <br> Requires Docker Compose Network |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Releasing

[Create a Release Issue](https://github.com/sovity/edc-broker-server-extension/issues/new?assignees=&labels=task%2Frelease%2Cscope%2Fmds&projects=&template=release.md&title=Release+x.x.x) and follow the instructions.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Deployment

### Deployment Units

| Deployment Unit                                                | Version / Details                                                           |
|----------------------------------------------------------------|-----------------------------------------------------------------------------|
| Reverse Proxy that merges the UI+Backend and removes the ports | (deployment specific)                                                       |
| Postgresql                                                     | 15 or compatible version                                                    |
| Broker Backend                                                 | broker-server-ce, see [CHANGELOG.md](CHANGELOG.md) for compatible versions. |
| Broker UI                                                      | edc-ui, see  [CHANGELOG.md](CHANGELOG.md) for compatible versions.          |

### Configuration

There is a [docker-compose.yaml](docker-compose.yaml) to try out the broker locally. However, a productive release will
require a few more configuration options, so you should only use it to check if the released version is roughly working
or if it's broken.

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
in [connector/.env](connector/.env).

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
