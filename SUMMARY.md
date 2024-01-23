# Table of contents

* [Start](README.md)
* [Connector Versions](launchers/README.md)
* [Changelog](CHANGELOG.md)
* [About sovity](https://sovity.de/en/sovity-en/)

## Introduction

* [What is a Data Space](docs/gitbook/what\_is\_a\_dataspace.md)

## Setup

* [Deployment Goals](setup/deployment-goals/README.md)
  * [Local Demo](docs/deployment-guide/goals/local-demo/README.md)
  * [Development](docs/deployment-guide/goals/development/README.md)
  * [Productive](setup/deployment-goals/productive/README.md)
    * [Deployment Guide](docs/deployment-guide/goals/production/README.md)
    * [Deployment Guide 4.2.0 / MS8 / MDS 1.2](docs/deployment-guide/goals/production/4.2.0/README.md)
* [Deployment Types](setup/deployment-types/README.md)
  * [Docker Image](https://github.com/sovity/edc-extensions/pkgs/container/edc-ce)
  * [Docker-compose](https://github.com/sovity/edc-extensions/blob/main/docker-compose.yaml)
* [Configuration](setup/configuration/README.md)
  * [Firewalls/Proxies](https://github.com/sovity/edc-extensions/tree/main/docs/deployment-guide/goals/production#configuration)
  * [Data Space Configuration](docs/gitbook/data\_space\_configuration.md)
* [Administration](setup/administration/README.md)
  * [Update Process](docs/gitbook/update\_process.md)
  * [Database Migration](extensions/postgres-flyway/README.md)
* [Troubleshooting & FAQ](https://github.com/sovity/edc-extensions/blob/main/docs/deployment-guide/goals/production/README.md#faq)
* [About Security](https://github.com/sovity/edc-extensions/security/policy)

## Developer

* [Architecture](https://eclipse-edc.github.io/docs/#/submodule/Connector/docs/developer/?id=architecture)
* [Data Model](https://github.com/eclipse-edc/Connector/blob/release/0.0.1-20230220.patch1-SNAPSHOT/docs/developer/architecture/domain-model.md#domain-model)
* [DSPACE Protocol](https://docs.internationaldataspaces.org/ids-knowledgebase/v/dataspace-protocol/overview/readme)
* [API documentation](developer/api-documentation/README.md)
  * [Management API](https://app.swaggerhub.com/apis/eclipse-edc-bot/management-api/0.2.1)
  * [Postman Collection](https://github.com/sovity/edc-extensions/blob/main/docs/postman\_collection.json)
* [Data Transfer Modes](docs/getting-started/documentation/data-transfer-methods.md)
  * [Data Proxy/Pull](docs/getting-started/documentation/pull-data-transfer.md)
* [Authentication](developer/authentication/README.md)
  * [API Key](docs/gitbook/api\_key.md)
  * [OAuth 2.0](docs/getting-started/documentation/oauth-data-address.md)
* [Extensions](developer/extensions/README.md)
  * [EDC UI Config](extensions/edc-ui-config/README.md)
  * [Last Commit Info](extensions/last-commit-info/README.md)
  * [Policies](developer/extensions/policies/README.md)
    * [Always True](extensions/policy-always-true/README.md)
    * [Referring Connector](extensions/policy-referring-connector/README.md)
    * [Time Interval](extensions/policy-time-interval/README.md)
  * [Postgres-flyway](extensions/postgres-flyway/README.md)
  * [API Wrapper](extensions/wrapper/README.md)
    * [Community Edition API](extensions/wrapper/wrapper-api/README.md)
    * [Enterprise Edition API](extensions/wrapper/wrapper-ee-api/README.md)
    * [Java API Client Library](extensions/wrapper/clients/java-client/README.md)
    * [Java API Client Library Example](extensions/wrapper/clients/java-client-example/README.md)
    * [TypeScript API Client Library](extensions/wrapper/clients/typescript-client/README.md)
    * [TypeScript API Client Library Example](extensions/wrapper/clients/typescript-client-example/README.md)

## Community

* [Code of Conduct](CODE\_OF\_CONDUCT.md)
* [Contribution Guide](CONTRIBUTING.md)
* [Code-Style Guide](STYLEGUIDE.md)
* [Security Guide](SECURITY.md)

## sovity's Connector-as-a-Service (CaaS)

* [CaaS Documentation on Zammad](https://sovity.zammad.com/#knowledge\_base/1/locale/en-us)
* [Comparison Table of offerings](https://sovity.de/en/connect-to-data-space-en/)
* [Versions](https://github.com/sovity/edc-extensions/releases)
