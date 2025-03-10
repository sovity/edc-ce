> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.deployment.kind`=`control-plane-standalone`

Deploys only a Control Plane. Requires at least one additionally deployed data plane to become a fully functioning EDC Connector.

## Choice: `sovity.dataspace.kind`=`catena-x`

Configures the connector Catena-X compliant and Tractus-X compatible while further enhancing it with sovity features

[Back](../README.md)

## Vault Entries

| Key                 | Example Value                 | Description                                    |
|---------------------|-------------------------------|------------------------------------------------|
| `sts-client-secret` | random characters and letters | The client secret for the IAM STS OAuth client |


## Config

### Important Config

| Name                                 | Required | Description                                                                                     |
|--------------------------------------|----------|-------------------------------------------------------------------------------------------------|
| `edc.iam.issuer.id`                  | Required | DID of this connector. Starts with did:web:                                                     |
| `edc.iam.sts.dim.url`                | Required | STS Dim endpoint                                                                                |
| `edc.iam.sts.oauth.client.id`        | Required | STS OAuth2 client id                                                                            |
| `edc.iam.sts.oauth.token.url`        | Required | STS OAuth2 endpoint for requesting a token                                                      |
| `edc.iam.trusted-issuer.cofinity.id` | Required | DID of the issuer, starts with did:web:                                                         |
| `edc.participant.id`                 | Required | Participant ID / Connector ID. Usually handed out by the dataspace operator for your connector. |
| `tx.iam.iatp.bdrs.server.url`        | Required | Base URL of the BDRS service                                                                    |


### Overrides / Ignore

<details><summary>Show / Hide (7)</summary>

| Name                                               | Required                                       | Description                                         |
|----------------------------------------------------|------------------------------------------------|-----------------------------------------------------|
| `edc.iam.iatp.default-scopes.governance.alias`     | Defaults to `org.eclipse.tractusx.vc.type`     | The alias of the scope 'governance'                 |
| `edc.iam.iatp.default-scopes.governance.operation` | Defaults to `read`                             | The operation of the scope 'governance' e.g. 'read' |
| `edc.iam.iatp.default-scopes.governance.type`      | Defaults to `DataExchangeGovernanceCredential` | The credential type of the scope 'governance'       |
| `edc.iam.iatp.default-scopes.membership.alias`     | Defaults to `org.eclipse.tractusx.vc.type`     | The alias of the scope 'membership'                 |
| `edc.iam.iatp.default-scopes.membership.operation` | Defaults to `read`                             | The operation of the scope 'membership' e.g. 'read' |
| `edc.iam.iatp.default-scopes.membership.type`      | Defaults to `MembershipCredential`             | The credential type of the scope 'membership'       |
| `edc.iam.sts.oauth.client.secret.alias`            | Defaults to `sts-client-secret`                | Vault alias for the STS oauth client secret         |


</details>

