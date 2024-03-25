Productive Deployment Guide
========

> This is for our latest version. There is another guide for [4.2.0](4.2.0/README.md).

## About this Guide

This is a productive deployment guide for self-hosting a functional sovity CE EDC Connector or MDS CE EDC Connector.

## Prerequisites

### Technical Skills

- Ability to deploy, run and expose containered applications to the internet.
- Ability to configure ingress routes or a reverse proxy of your choice to merge multiple services under a single
  domain.
- Know-how on how to secure an otherwise unprotected application with an auth proxy or other solutions fitting
  your situation.

### Dataspace

- Must have a running DAPS that follows the subset of OAuth2 as described in the DSP Specification.
- You must have a valid Connector Certificate in the form of [a generated SKI/AKI pair and .jks file](#faq).
- You must have a valid Participant ID / Connector ID, which is configured in the claim "referringConnector" in the
  DAPS.

## Deployment Units

To deploy an EDC multiple deployment units must be deployed and configured.

| Deployment Unit                                                   | Version / Details                                                                           |
|-------------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| An Auth Proxy / Auth solution of your choice.                     | (deployment specific, required to secure UI and management API)                             |
| Reverse Proxy that merges multiple services and removes the ports | (deployment specific)                                                                       |
| Postgresql                                                        | 13 or compatible version                                                                    |
| EDC Backend                                                       | edc-ce or edc-ce-mds, see [CHANGELOG.md](../../../../CHANGELOG.md) for compatible versions. |
| EDC UI                                                            | edc-ui, see  [CHANGELOG.md](../../../../CHANGELOG.md) for compatible versions.              |

## Configuration

### Reverse Proxy Configuration

To make the deployment work, the connector needs to be exposed to the internet. Connector-to-Connector
communication is asynchronous and done with authentication via the DAPS. Thus, if the target connector cannot reach
your connector under its self-declared URLs, contract negotiation and transfer processes will fail.

The EDC Backend opens up multiple ports with different functionalities. They are expected to be merged by a reverse
proxy (at least the protocol endpoint needs to be).

- The sovity EDC Connector is meant to be deployed with a reverse proxy merging the following ports:
    - The UI's `8080` port. Henceforth, called the UI.
    - The Backend's `11002` port. Henceforth, called the Management API.
    - The Backend's `11003` port. Henceforth, called the Protocol API.
- The mapping should look like this:
    - `https://[MY_EDC_FQDN]/api/dsp` -> `edc:11003/api/dsp`
    - `https://[MY_EDC_FQDN]/api/management` -> **Auth Proxy** -> `edc:11002/api/management`
    - All other requests -> **Auth Proxy** -> `edc-ui:80`
- Regarding TLS/HTTPS:
    - All endpoints need to be secured by TLS/HTTPS. A productive connector won't work without it.
    - All endpoint should have HTTP to HTTPS redirects.
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
EDC_UI_MANAGEMENT_API_URL: https://[EDC URL]/api/management

# Management API Key
EDC_UI_MANAGEMENT_API_KEY: "ApiKeyDefaultValue"

# Enable config fetching from the backend
EDC_UI_CONFIG_URL: "edc-ui-config"
```

You can also optionally set the following config properties:
```yaml
# Override the management API URL shown to the user in the UI
EDC_UI_MANAGEMENT_API_URL_SHOWN_IN_DASHBOARD: https://[EDC_URL]/api/control/management
```

## EDC Backend Configuration

A sovity EDC CE or MDS EDC CE Backend deployment requires the following environment variables:

> [!WARNING]
> Please be careful with overriding any of the ENV Vars set in our [launchers/.env](../../../../launchers/.env). Our
> defaults
> will respect overrides, and the Core EDC ENV Vars can be in some cases sensitive to edge cases such as trailing
> slashes.

```yaml
# Connector Host Name
MY_EDC_FQDN: "my-edc-deployment1.example.com"

# Participant ID / Connector ID
# Must be configured as the value of the "referringConnector" claim in the DAPS for this connector
MY_EDC_PARTICIPANT_ID: "MDSL1234XX.C1234XX"

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
3. Run the [script](./generate_ski_aki.sh) `./generate_ski_aki.sh [filepath].p12 [password]` and
   substitute `[filepath]` to the p12 certificate
   filepath and `[password]` to the certificate password
4. The jks file will be generated in the same folder as your p12 file and the SKI/AKI combination is printed out in the
   console.
   Copy the SKI:AKI combination and use it to start the EDC (optionally also save it to your password manager).
5. The generated `.jks` file needs to be mounted into your productive running container.

### Can I run a connector locally and consume data from an online connector?

No, locally run connectors cannot exchange data with online connectors. A connector must have a proper URL +
configuration and be accesible from the data provider via REST calls.

### Can I use a different DAT Claim for the Participant ID verification?

The checked DAT claim name can be changed by overriding `EDC_AGENT_IDENTITY_KEY`. However, this must be done in sync
with all connectors of the data space for contract negotations and transfers to work.

### Can I change the Participant ID of my connector?

You can always re-start your connector with a different Participant ID. Please make sure your changed Participant ID is
deposited in the DAPS as new Contract Negotiations or Transfer Processes will validate the Participant ID of each
connector. Both connectors must also be configured to check for the same claim.

After changing your Participant ID old Contract Agreements will stop working, because the Participant ID is heavily
referenced in both connectors, and there is no way for the other connector to know what your Participant ID changed to.

This is relevant, because for MS8 connectors the Participant ID concept did not exist yet or was not enforced in any
way, which might force participants to re-negotiate old contracts.

### What if I have no Participant ID / Connector ID concept in my Dataspace?

If there is no Participant ID / Connector ID concept in your Dataspace, you could use the AKI / SKI Client ID as
Participant ID / Connector ID:

```yaml
# Using the SKI / AKI Client ID as Participant ID
MY_EDC_PARTICIPANT_ID: '_your SKI/AKI_'

# Claim Name of the AKI / SKI Client ID:
EDC_AGENT_IDENTITY_KEY: 'sub' # or 'client_id' in Omejdn
```

The downside to doing this is that the AKI / SKI Client ID is not human-readable, but will be shown in many places.

### Can I still use the deprecated Omejdn DAPS?

In the current version of the sovity EDC CE Connector the Omejdn DAPS is not supported due to the Omejdn DAPS requiring
a special OAuth2 extension and custom messages that exceed the default DSP Oauth2 Specification.

When using the required extension, these additional env variables would be required for the backend to be configured for
the Omejdn DAPS:

```yaml
# Required Config for an Omejdn DAPS:
MY_EDC_PARTICIPANT_ID: '_your SKI/AKI_'
EDC_AGENT_IDENTITY_KEY: 'client_id'
EDC_OAUTH_PROVIDER_AUDIENCE: 'idsc:IDS_CONNECTORS_ALL'
EDC_OAUTH_ENDPOINT_AUDIENCE: 'idsc:IDS_CONNECTORS_ALL'
```
