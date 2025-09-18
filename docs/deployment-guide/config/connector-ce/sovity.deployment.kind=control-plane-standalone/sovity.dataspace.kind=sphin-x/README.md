> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.deployment.kind`=`control-plane-standalone`

Deploys only a Control Plane. Requires at least one additionally deployed data plane to become a fully functioning EDC Connector.

## Choice: `sovity.dataspace.kind`=`sphin-x`

Configures the connector for use in Sphin-X.

[Back](../README.md)

## Vault Entries

| Key                 | Example Value                 | Description                                    |
|---------------------|-------------------------------|------------------------------------------------|
| `sts-client-secret` | random characters and letters | The client secret for the IAM STS OAuth client |


## Config

### Important Config

| Name                               | Required | Description                                                                                                                                                                                       |
|------------------------------------|----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `edc.iam.issuer.id`                | Required | DID of this connector. Starts with did:web:<br>In sphinx this is the Connector URI/DID                                                                                                            |
| `edc.iam.sts.oauth.client.id`      | Required | STS OAuth2 client id<br>In sphin-X with spherity wallets this is the first component before the `:` of the `apiKeyId`. The second component is then the API Key and must be input into the vault. |
| `edc.iam.sts.oauth.token.url`      | Required | STS OAuth2 endpoint for requesting a token<br>STS API Endpoint on the Wallet                                                                                                                      |
| `edc.iam.trusted-issuer.sphinx.id` | Required | DID of the issuer, starts with did:web:<br>This is the DID of the issuer used for this sphinx dataspace environment                                                                               |


### Overrides / Ignore

> [!WARNING]
> The properties in this section are mentioned for completeness and are not supported.
> They are present here because we use them internally.
> Expect them to change without notice.

<details><summary>Show / Hide (7)</summary>

| Name                                                  | Required                                   | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
|-------------------------------------------------------|--------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `edc.iam.sts.oauth.client.secret.alias`               | Defaults to `sts-client-secret`            | Vault alias for the STS oauth client secret                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `edc.participant.id`                                  | Defaults to value from `edc.iam.issuer.id` | Participant ID / Connector ID. Usually handed out by the dataspace operator for your connector.<br>In sphinx this is the Connector URI/DID                                                                                                                                                                                                                                                                                                                                                                        |
| `sovity.edc.ui.features.add.SPHINX_ASSET_METADATA`    | Defaults to `true`                         | Filled out wildcard property `sovity.edc.ui.features.add.*` with value `SPHINX_ASSET_METADATA`. Set to `true` to individually enable the given EDC UI Feature. Not all given available values are supported by the Community Edition.<br><br>Available values for the asterisk:<br> * `CONNECTOR_LIMITS`<br> * `OPEN_SOURCE_MARKETING`<br> * `EE_BASIC_MARKETING`<br> * `CATENA_POLICIES`<br> * `SOVITY_POLICIES`<br> * `SPHINX_POLICIES`<br> * `SPHINX_ASSET_METADATA`<br> * `BUSINESS_PARTNER_GROUP_MANAGEMENT` |
| `sovity.edc.ui.features.add.SPHINX_POLICIES`          | Defaults to `true`                         | Filled out wildcard property `sovity.edc.ui.features.add.*` with value `SPHINX_POLICIES`. Set to `true` to individually enable the given EDC UI Feature. Not all given available values are supported by the Community Edition.<br><br>Available values for the asterisk:<br> * `CONNECTOR_LIMITS`<br> * `OPEN_SOURCE_MARKETING`<br> * `EE_BASIC_MARKETING`<br> * `CATENA_POLICIES`<br> * `SOVITY_POLICIES`<br> * `SPHINX_POLICIES`<br> * `SPHINX_ASSET_METADATA`<br> * `BUSINESS_PARTNER_GROUP_MANAGEMENT`       |
| `tx.edc.iam.iatp.default-scopes.membership.alias`     | Defaults to `org.eclipse.tractusx.vc.type` | The alias of the scope 'membership'                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `tx.edc.iam.iatp.default-scopes.membership.operation` | Defaults to `read`                         | The operation of the scope 'membership' e.g. 'read'                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| `tx.edc.iam.iatp.default-scopes.membership.type`      | Defaults to `MembershipCredential`         | The credential type of the scope 'membership'                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |


</details>

