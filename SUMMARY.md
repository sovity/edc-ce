# Summary

* [Start](./README.md)
* [Connector Versions](./launchers/README.md)
* [Changelog](./CHANGELOG.md)
* [Roadmap](./docs/gitbook/roadmap.md) <!-- TODO -->
* [About sovity](https://sovity.de/en/sovity-en/)

## Introduction
* [What is a Data Space](./docs/gitbook/what_is_a_dataspace.md) <!-- TODO -->
* [What is a Connector](./docs/gitbook/what_is_a_connector.md) <!-- TODO -->
* [What is a Catalog](./docs/gitbook/what_is_a_catalog.md) <!-- TODO -->
* [What is a DAPS](./docs/gitbook/what_is_a_daps.md) <!-- TODO -->

## Setup
* Deployment Goals
    * [Local Demo](./docs/deployment-guide/goals/local-demo)
    * [Development](./docs/deployment-guide/goals/development)
    * Productive
        * [Deployment Guide](./docs/deployment-guide/goals/production)
        * [Deployment Guide 4.2.0 / MS8 / MDS 1.2](docs/deployment-guide/goals/production/4.2.0/README.md)
* Deployment Types
    * [Docker Image](https://github.com/sovity/edc-extensions/pkgs/container/edc-ce)
    * [HELM](./docs/gitbook/helm.md) <!-- TODO -->
    * [Docker-compose](https://github.com/sovity/edc-extensions/blob/main/docker-compose.yaml)
* Configuration
    * [Settings](./docs/gitbook/settings.md) <!-- TODO -->
    * [Firewalls/Proxies](https://github.com/sovity/edc-extensions/tree/main/docs/deployment-guide/goals/production#configuration)
    * [Data Space Configuration](./docs/gitbook/data_space_configuration.md) <!-- TODO -->
* Administration
    * [Update Process](./docs/gitbook/update_process.md) <!-- TODO -->
    * [Database Migration](./extensions/postgres-flyway/README.md)
* [Troubleshooting & FAQ](https://github.com/sovity/edc-extensions/blob/main/docs/deployment-guide/goals/production/README.md#faq)
* [About Security](https://github.com/sovity/edc-extensions/security/policy)

## Developer
* [Architecture](https://eclipse-edc.github.io/docs/#/submodule/Connector/docs/developer/?id=architecture)
* [Data Model](https://github.com/eclipse-edc/Connector/blob/release/0.0.1-20230220.patch1-SNAPSHOT/docs/developer/architecture/domain-model.md#domain-model)
* [DSPACE Protocol](https://docs.internationaldataspaces.org/ids-knowledgebase/v/dataspace-protocol/overview/readme)
* API documentation
    * [Management API](https://app.swaggerhub.com/apis/eclipse-edc-bot/management-api/0.2.1)
    * [Wrapper API](./docs/gitbook/wrapper_api.md) <!-- TODO -->
        * [Create Asset](./docs/gitbook/create_asset.md) <!-- TODO -->
        * [Create Policy](./docs/gitbook/create_policy.md) <!-- TODO -->
        * [Create Contract Definition](./docs/gitbook/create_contract_definition.md) <!-- TODO -->
    * [Postman Collection](https://github.com/sovity/edc-extensions/blob/main/docs/postman_collection.json)
* [Data Transfer Modes](./docs/getting-started/documentation/data-transfer-methods.md)
    * [Data Push](./docs/gitbook/data_push.md) <!-- TODO -->
    * [Data Proxy/Pull](./docs/getting-started/documentation/pull-data-transfer.md)
    * [Data out of band](./docs/gitbook/data_out_of_band.md) <!-- TODO -->
* [Authentication](./docs/gitbook/authentication.md) <!-- TODO -->
    * [API Key](./docs/gitbook/api_key.md) <!-- TODO -->
    * [Basic Auth](./docs/gitbook/basic_auth.md) <!-- TODO -->
    * [Custom Headers](./docs/gitbook/custom_headers.md) <!-- TODO -->
    * [OAuth 2.0](./docs/getting-started/documentation/oauth-data-address.md)
* Extensions
    * [EDC UI Config](./extensions/edc-ui-config/README.md)
    * [Last Commit Info](./extensions/last-commit-info/README.md)
    * Policies
        * [Always True](./extensions/policy-always-true/README.md)
        * [Referring Connector](./extensions/policy-referring-connector/README.md)
        * [Time Interval](./extensions/policy-time-interval/README.md)
    * [Postgres-flyway](./extensions/postgres-flyway/README.md)
    * [API Wrapper](./extensions/wrapper/README.md)
        * [Community Edition API](./extensions/wrapper/wrapper-api/README.md)
        * [Enterprise Edition API](./extensions/wrapper/wrapper-ee-api/README.md)
        * [Java API Client Library](./extensions/wrapper/clients/java-client/README.md)
        * [Java API Client Library Example](./extensions/wrapper/clients/java-client-example/README.md)
        * [TypeScript API Client Library](./extensions/wrapper/clients/typescript-client/README.md)
        * [TypeScript API Client Library Example](./extensions/wrapper/clients/typescript-client-example/README.md)

## Community
* [Code of Conduct](./CODE_OF_CONDUCT.md)
* [Contribution Guide](./CONTRIBUTING.md)
* [Code-Style Guide](./STYLEGUIDE.md)
* [Security Guide](./SECURITY.md)

## How Tos & FAQ
* [Notifications](./docs/gitbook/notifications.md) <!-- TODO -->
* [Integration: Use Case Pattern](./docs/gitbook/integration_use_case_pattern.md) <!-- TODO -->

## sovity's Connector-as-a-Service (CaaS)
* [Comparison Table of offerings](https://sovity.de/en/connect-to-data-space-en/)
* [Versions](https://github.com/sovity/edc-extensions/releases)
