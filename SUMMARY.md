# Summary

* [Start](./README.md)
* [Connector Versions](./launchers/README.md)
* [Changelog](./CHANGELOG.md)
* [Roadmap](./docs/gitbook/roadmap.md)
* [About sovity](https://sovity.de/en/sovity-en/)

## Introduction
* [What is a Data Space](./docs/gitbook/what_is_a_dataspace.md)
* [What is a Connector](./docs/gitbook/what_is_a_connector.md)
* [What is a Catalog](./docs/gitbook/what_is_a_catalog.md)
* [What is a DAPS](./docs/gitbook/what_is_a_daps.md)

## Setup
* Deployment Goals
    * [Local Demo](./docs/deployment-guide/goals/local-demo)
    * [Development](./docs/deployment-guide/goals/development)
    * Productive
        * [Deployment Guide](./docs/deployment-guide/goals/production)
        * [Deployment Guide 4.2.0 / MS8 / MDS 1.2](docs/deployment-guide/goals/production/4.2.0/README.md)
* Deployment Types
    * [Docker Image](./docs/gitbook/docker_image.md)
    * [HELM](./docs/gitbook/helm.md)
    * [Docker-compose](https://github.com/sovity/edc-extensions/blob/main/docker-compose.yaml)
* Configuration
    * [Settings](./docs/gitbook/settings.md)
    * [Firewalls/Proxies](./docs/gitbook/firewall_proxies.md)
    * [Data Space Configuration](./docs/gitbook/data_space_configuration.md)
* Administration
    * [Update Process](./docs/gitbook/update_process.md)
    * [Database Migration](./extensions/postgres-flyway/README.md)
* [Troubleshooting & FAQ](./docs/gitbook/troubleshooting_faq.md)
* [About Security](./docs/gitbook/about_security.md)

## Developer
* [Architecture](./docs/gitbook/architecture.md)
* [Data Model](https://github.com/eclipse-edc/Connector/blob/release/0.0.1-20230220.patch1-SNAPSHOT/docs/developer/architecture/domain-model.md#domain-model)
* [DSPACE Protocol](https://docs.internationaldataspaces.org/ids-knowledgebase/v/dataspace-protocol/overview/readme)
* API documentation
    * [Management API](./docs/gitbook/management_api.md)
    * [Wrapper API](./docs/gitbook/wrapper_api.md)
        * [Create Asset](./docs/gitbook/create_asset.md)
        * [Create Policy](./docs/gitbook/create_policy.md)
        * [Create Contract Definition](./docs/gitbook/create_contract_definition.md)
    * [Postman Collection](https://github.com/sovity/edc-extensions/blob/main/docs/postman_collection.json)
* [Data Transfer Modes](./docs/getting-started/documentation/data-transfer-methods.md)
    * [Data Push](./docs/gitbook/data_push.md)
    * [Data Proxy/Pull](./docs/getting-started/documentation/pull-data-transfer.md)
    * [Data out of band](./docs/gitbook/data_out_of_band.md)
* [Authentication](./docs/gitbook/authentication.md)
    * [API Key](./docs/gitbook/api_key.md)
    * [Basic Auth](./docs/gitbook/basic_auth.md)
    * [Custom Headers](./docs/gitbook/custom_headers.md)
    * [OAuth 2.0](./docs/getting-started/documentation/oauth-data-address.md)
* Extensions
    * [EDC UI Config](./extensions/edc-ui-config/README.md)
    * [Last Commit Info](./extensions/last-commit-info/README.md)
    * Policies
        * [Always True](./extensions/policy-always-true/README.md)
        * [Referring Connector](./extensions/policy-referring-connector/README.md)
        * [Time Interval](./extensions/policy-time-interval/README.md)
    * [Postgres-flyway](./docs/gitbook/postgres_flyway.md)
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
* [Notifications](./docs/gitbook/notifications.md)
* [Integration: Use Case Pattern](./docs/gitbook/integration_use_case_pattern.md)

## sovity's Connector-as-a-Service (CaaS)
* [Comparison Table of offerings](./docs/gitbook/comparison_table_of_offerings.md)
* [Versions](./docs/gitbook/versions.md)
