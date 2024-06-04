Deployment Goal: Production
========

> This is for an old major version sovity EDC CE 4.2.0. [Go back](../README.md)

## About this Guide

This is a productive deployment guide for self-hosting a functional sovity CE EDC Connector or MDS CE EDC Connector.

## Requirements

A productive EDC Connector deployment has strict requirements, with slight errors in configuration already causing
contract negotiations / data transfer to fail.

In general a productive EDC Connector requires a DAPS Server, DAPS Credentials, a reverse proxy configured in detail due
to technical reasons, reachability via the internet and well-defined URLs across all configurations.

## Deployment Units

To deploy an EDC multiple deployment units must be deployed and configured.

| Deployment Unit                                                | Version / Details                                                                              |
|----------------------------------------------------------------|------------------------------------------------------------------------------------------------|
| An Auth Proxy / Auth solution of your choice.                  | (deployment specific, required to secure UI and management API)                                |
| Reverse Proxy that merges the UI+Backend and removes the ports | (deployment specific)                                                                          |
| Postgresql                                                     | 13 or compatible version                                                                       |
| EDC Backend                                                    | edc-ce or edc-ce-mds, see [CHANGELOG.md](../../../../../CHANGELOG.md) for compatible versions. |
| EDC UI                                                         | edc-ui, see  [CHANGELOG.md](../../../../../CHANGELOG.md) for compatible versions.              |

## Configuration

### Reverse Proxy Configuration

To make the deployment work, the connector needs to be exposed to the internet. Connector-to-Connector
communication is asynchronous and done with authentication via the DAPS. Thus, if the target connector cannot reach
your connector under its self-declared URLs, contract negotiation and transfer processes will fail.

The EDC Backend opens up multiple ports with different functionalities. They are expected to be merged by a reverse
proxy (at least the protocol endpoint needs to be).

- The sovity EDC Connector is meant to be deployed with a reverse proxy merging the following ports:
    - The UI's `80` port. Henceforth, called the UI.
    - The Backend's `11002` port. Henceforth, called the Management API.
    - The Backend's `11003` port. Henceforth, called the Protocol API.
- The mapping should look like this:
    - `/api/v1/ids` -> `edc:11003/api/v1/ids`
    - `/api/v1/management` -> `edc:11002/api/v1/management`
    - All other requests should be mapped to `edc-ui:80`
- Regarding TLS/HTTPS:
    - All endpoints need to be secured by TLS/HTTPS. A productive connector won't work without it.
    - The UI and the Management API should have HTTP to HTTPS redirects.
    - The Protocol API must allow HTTP traffic to pass through. This is due to some loopback requests
      mistakenly using HTTP instead of HTTPS that would otherwise be blocked or have their credentials wiped.
- Regarding Authentication:
    - The UI and the Management API need to be secured by an auth proxy. Otherwise, access to either would mean full
      control of the application.
    - The backend's `11003` port needs to be unsecured. Authentication between connectors is done via the Data Space
      Authority / DAPS and the configured certificates.
- Exposing to the internet:
    - The Protocol API must be reachable via the internet. The required endpoints can be found in
      this [public-endpoints.yaml](public-endpoints.yaml)
    - Exposing the UI or the Management Endpoint to the internet requires an intermediate auth proxy, we recommend restricting the access to the Management Endpoint to your internal network.
- Security:
    - Limit the header size in the proxy so that only a certain number of API Keys can be tested with one API-request (e.g. limit to 8kb).
    - Limit the access rate to the API endpoints and monitor access for attacks like brute force attacks.

## EDC UI Configuration

A sovity EDC UI deployment requires the following config properties:

```yaml
# Active Profile
EDC_UI_ACTIVE_PROFILE: sovity-open-source (or mds-open-source)

# Management API URL
EDC_UI_DATA_MANAGEMENT_API_URL: https://[EDC URL]/api/v1/management

# Management API Key
EDC_UI_DATA_MANAGEMENT_API_KEY: "ApiKeyDefaultValue"

# Enable config fetching from the backend
EDC_UI_CONFIG_URL: "edc-ui-config"
```

## EDC Backend Configuration

A sovity EDC CE or MDS EDC CE Backend deployment requires:

- A running DAPS
- (MDS Only) A running Clearing House
- DAPS Access
  and [a generated SKI/AKI pair and .jks file](#faq)
- The following configuration properties

> [!WARNING]
> Please be careful with overriding any of the ENV Vars set in our [launchers/.env.connector](../../../../../connector/launchers/.env.connector). 
> Our defaults will respect overrides, and the Core EDC ENV Vars can be in some cases sensitive to edge cases such as 
> trailing slashes.

```yaml
# Connector Host Name
MY_EDC_FQDN: "my-edc-deployment1.example.com"

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

# Database Connection
MY_EDC_JDBC_URL: jdbc:postgresql://postgresql:5432/edc
MY_EDC_JDBC_USER: edc
MY_EDC_JDBC_PASSWORD: edc

# Management API Key
# high entropy recommended when configuring the value (length, complexity, e.g. [a-zA-Z0-9+special chars]{32+ chars})
EDC_API_AUTH_KEY: ApiKeyDefaultValue

# Connector Maintainer
# The company hosting the EDC Connector
MY_EDC_MAINTAINER_URL: "https://sovity.de"
MY_EDC_MAINTAINER_NAME: "sovity GmbH"

# (MDS Only) Clearing House
EDC_CLEARINGHOUSE_LOG_URL: 'https://clearing.test.mobility-dataspace.eu/messages/log'

# DAPS URL
EDC_OAUTH_TOKEN_URL: 'https://daps.test.mobility-dataspace.eu/token'
EDC_OAUTH_PROVIDER_JWKS_URL: 'https://daps.test.mobility-dataspace.eu/jwks.json'

# DAPS Credentials
EDC_OAUTH_CLIENT_ID: '_your SKI/AKI_'
EDC_KEYSTORE: '_path to .jks file in container_'
EDC_KEYSTORE_PASSWORD: '_your keystore password_'
EDC_OAUTH_CERTIFICATE_ALIAS: 1
EDC_OAUTH_PRIVATE_KEY_ALIAS: 1
```

## FAQ

### What should the client ID entry look like?

Example of a client-ID entry:

`EDC_OAUTH_CLIENT_ID: 7X:7Y:...:B2:94:keyid:6A:2B:...:28:80`

### How do you get the SKI and AKI of a p12 and how do you convert it to a jks?

You can use a script (if you're on WSL or Linux) to generate the SKI, AKI and jks file.

1. Make sure you're on Linux or on a bash console (e.g. WSL or Git Bash) and have openssl and keytool installed
2. Navigate in the console to the resources/docs directory
3. Run the [script](../generate_ski_aki.sh) `./generate_ski_aki.sh [filepath].p12 [password]` and
   substitute `[filepath]` to the p12 certificate
   filepath and `[password]` to the certificate password
4. The jks file will be generated in the same folder as your p12 file and the SKI/AKI combination is printed out in the
   console.
   Copy the SKI:AKI combination and use it to start the EDC (optionally also save it to your password manager).
5. The generated `.jks` file needs to be mounted into your productive running container.

### Can I run a connector locally and consume data from an online connector?

No, locally run connectors cannot exchange data with online connectors. A connector must have a proper URL +
configuration and be accessible from the data provider via REST calls.

### (MDS Only) Can I disable the Broker- and/or ClearingHouse-Client-Extensions dynamically?

Yes, if the two extensions are included, they can still be disabled via properties.
The default settings can be found in `docker-compose.yaml` and can be changed there.

```yaml
# Extension Configuration
BROKER_CLIENT_EXTENSION_ENABLED: false # disabled by default
CLEARINGHOUSE_CLIENT_EXTENSION_ENABLED: true # enabled by default
```
