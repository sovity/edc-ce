> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.deployment.kind`=`control-plane-standalone`

Deploys only a Control Plane. Requires at least one additionally deployed data plane to become a fully functioning EDC Connector.

## Choice: `sovity.management.api.iam.kind`=`management-iam-api-key`

Legacy API Key Auth for the Management API

[Back](../README.md)

## Config

### Important Config

| Name               | Required                                                              | Description                                              |
|--------------------|-----------------------------------------------------------------------|----------------------------------------------------------|
| `edc.api.auth.key` | Required in production. Defaults to `ApiKeyDefaultValue` outside prod | Management API: API Key, provided with Header X-Api-Key. |


