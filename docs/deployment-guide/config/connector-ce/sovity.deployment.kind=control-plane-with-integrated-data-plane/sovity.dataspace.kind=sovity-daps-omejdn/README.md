> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.deployment.kind`=`control-plane-with-integrated-data-plane`

Deploys an EDC Connector with both a Control Plane and an integrated Data Plane.

## Choice: `sovity.dataspace.kind`=`sovity-daps-omejdn`

Configures the connector for use in legacy self-hosted sovity dataspaces that use the now deprecated Omejdn DAPS

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

> [!WARNING]
> The properties in this section are mentioned for completeness and are not supported.
> They are present here because we use them internally.
> Expect them to change without notice.

<details><summary>Show / Hide (9)</summary>

| Name                                           | Required                              | Description                                                                                                                                                                                                                                                                                                                                                                                                 |
|------------------------------------------------|---------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `edc.agent.identity.key`                       | Defaults to `client_id`               | OAuth2 / DAPS: Access token claim name that must coincide with the Participant ID                                                                                                                                                                                                                                                                                                                           |
| `edc.oauth.certificate.alias`                  | Defaults to `daps-cert`               | OAuth2 / DAPS: Vault Entry: DAPS C2C IAM Certificate                                                                                                                                                                                                                                                                                                                                                        |
| `edc.oauth.endpoint.audience`                  | Defaults to `idsc:IDS_CONNECTORS_ALL` | OAuth2 / DAPS: Endpoint Audience                                                                                                                                                                                                                                                                                                                                                                            |
| `edc.oauth.private.key.alias`                  | Defaults to `daps-priv`               | OAuth2 / DAPS: Vault Entry: DAPS C2C IAM Private Key                                                                                                                                                                                                                                                                                                                                                        |
| `edc.oauth.provider.audience`                  | Defaults to `idsc:IDS_CONNECTORS_ALL` | OAuth2 / DAPS: Provider Audience                                                                                                                                                                                                                                                                                                                                                                            |
| `edc.oauth.validation.issued.at.leeway`        | Defaults to `10`                      | OAuth2 / DAPS: Leeway for the 'iat' claim in seconds                                                                                                                                                                                                                                                                                                                                                        |
| `edc.oauth.validation.nbf.leeway`              | Defaults to `10`                      | OAuth2 / DAPS: Leeway for the 'nbf' claim in seconds                                                                                                                                                                                                                                                                                                                                                        |
| `sovity.contract.termination.thread.pool_size` | Defaults to `10`                      | The number of contracts messages that can be simultaneously processed                                                                                                                                                                                                                                                                                                                                       |
| `sovity.edc.ui.features.add.SOVITY_POLICIES`   | Defaults to `true`                    | Filled out wildcard property `sovity.edc.ui.features.add.*` with value `SOVITY_POLICIES`. Set to `true` to individually enable the given EDC UI Feature. Not all given available values are supported by the Community Edition.<br><br>Available values for the asterisk:<br> * `CONNECTOR_LIMITS`<br> * `OPEN_SOURCE_MARKETING`<br> * `EE_BASIC_MARKETING`<br> * `CATENA_POLICIES`<br> * `SOVITY_POLICIES` |


</details>

