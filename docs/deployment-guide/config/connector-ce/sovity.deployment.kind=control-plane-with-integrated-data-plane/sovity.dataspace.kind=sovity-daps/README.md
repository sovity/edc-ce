> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.deployment.kind`=`control-plane-with-integrated-data-plane`

Deploys an EDC Connector with both a Control Plane and an integrated Data Plane.

## Choice: `sovity.dataspace.kind`=`sovity-daps`

Configures the connector for use in sovity dataspaces that use the sovity DAPS

[Back](../README.md)

## Vault Entries

| Key         | Example Value                                                       | Description              |
|-------------|---------------------------------------------------------------------|--------------------------|
| `daps-cert` | PEM/Base64 format `-----BEGIN CERTIFICATE-----`                     | DAPS C2C IAM Certificate |
| `daps-priv` | [PKCS 8](https://en.wikipedia.org/wiki/PKCS_8) in PEM/Base64 format | DAPS C2C IAM Private Key |


## Config

### Important Config

| Name                          | Required                                    | Description                                                                                     |
|-------------------------------|---------------------------------------------|-------------------------------------------------------------------------------------------------|
| `edc.oauth.client.id`         | Defaults to value from `edc.participant.id` | OAuth2 / DAPS: Client ID                                                                        |
| `edc.oauth.provider.jwks.url` | Required                                    | OAuth2 / DAPS: JWKS URL                                                                         |
| `edc.oauth.token.url`         | Required                                    | OAuth2 / DAPS: Token URL                                                                        |
| `edc.participant.id`          | Required                                    | Participant ID / Connector ID. Usually handed out by the dataspace operator for your connector. |


### Overrides / Ignore

<details><summary>Show / Hide (5)</summary>

| Name                          | Required                                     | Description                                                                       |
|-------------------------------|----------------------------------------------|-----------------------------------------------------------------------------------|
| `edc.agent.identity.key`      | Defaults to `azp`                            | OAuth2 / DAPS: Access token claim name that must coincide with the Participant ID |
| `edc.oauth.certificate.alias` | Defaults to `daps-cert`                      | OAuth2 / DAPS: Vault Entry: DAPS C2C IAM Certificate                              |
| `edc.oauth.endpoint.audience` | Defaults to `edc:dsp-api`                    | OAuth2 / DAPS: Endpoint Audience                                                  |
| `edc.oauth.private.key.alias` | Defaults to `daps-priv`                      | OAuth2 / DAPS: Vault Entry: DAPS C2C IAM Private Key                              |
| `edc.oauth.provider.audience` | Defaults to value from `edc.oauth.token.url` | OAuth2 / DAPS: Provider Audience                                                  |


</details>

