# Catalog Crawler Productive Deployment Guide

## About this Guide

This is a productive deployment guide for self-hosting a single [catalog crawler](../../../../extensions/catalog-crawler/README.md).

One catalog crawler per Authority Portal Deployment Environment must be deployed.

## Deployment Units

| Deployment Unit | Version / Details                                                                                       |
|-----------------|---------------------------------------------------------------------------------------------------------|
| Catalog Crawler | see the changelog for available versions. See the Authority Portal's Changelog for compatible versions. |

#### Reverse Proxy Configuration

- The catalog crawler is meant to be served via TLS/HTTPS.
- The catalog crawler is meant to be deployed with a reverse proxy terminating TLS / providing HTTPS.
- All requests are meant to be redirected to the deployment's `11003` port.

#### Catalog Crawler Configuration

A productive configuration will require you to join a DAPS.

For that you will need a SKI/AKI client ID. Please refer
to [edc-extension's Getting Started Guide](https://github.com/sovity/edc-ce/tree/main/docs/getting-started#faq)
on how to generate one.

The DAPS needs to contain the claim `referringConnector=broker` for the broker.
The expected value `broker` could be overridden by specifying a different value for `MY_EDC_PARTICIPANT_ID`.

```yaml
# Required: Fully Qualified Domain Name
MY_EDC_FQDN: "crawler.test.example.com"

# Required: Authority Portal Environment ID
CRAWLER_ENVIRONMENT_ID: test

# Required: Authority Portal Postgresql DB Access
CRAWLER_DB_JDBC_URL: jdbc:postgresql://authority-portal:5432/portal
CRAWLER_DB_JDBC_USER: portal
CRAWLER_DB_JDBC_PASSWORD: portal

# Required: DAPS credentials
EDC_OAUTH_TOKEN_URL: 'https://daps.test.mobility-dataspace.eu/token'
EDC_OAUTH_PROVIDER_JWKS_URL: 'https://daps.test.mobility-dataspace.eu/jwks.json'
EDC_OAUTH_CLIENT_ID: '_your SKI/AKI_'
EDC_KEYSTORE: '_your keystore file_' # Needs to be available as file in the running container
EDC_KEYSTORE_PASSWORD: '_your keystore password_'
EDC_OAUTH_CERTIFICATE_ALIAS: 1
EDC_OAUTH_PRIVATE_KEY_ALIAS: 1
```

Additional available configuration options can be found
in [launcher/.env.catalog-crawler](../../../../launchers/.env.catalog-crawler).
