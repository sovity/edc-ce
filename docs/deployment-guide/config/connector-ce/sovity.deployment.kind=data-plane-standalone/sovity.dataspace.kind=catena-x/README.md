> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.deployment.kind`=`data-plane-standalone`

Deploys only a Data Plane. Depends on a control plane.

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
| `edc.iam.sts.oauth.client.id`        | Required | STS OAuth2 client id                                                                            |
| `edc.iam.sts.oauth.token.url`        | Required | STS OAuth2 endpoint for requesting a token                                                      |
| `edc.iam.trusted-issuer.cofinity.id` | Required | DID of the issuer, starts with did:web:                                                         |
| `edc.participant.id`                 | Required | Participant ID / Connector ID. Usually handed out by the dataspace operator for your connector. |
| `tx.edc.iam.sts.dim.url`             | Required | STS Dim endpoint                                                                                |
| `tx.iam.iatp.bdrs.server.url`        | Required | Base URL of the BDRS service                                                                    |


### Overrides / Ignore

> [!WARNING]
> The properties in this section are mentioned for completeness and are not supported.
> They are present here because we use them internally.
> Expect them to change without notice.

<details><summary>Show / Hide (11)</summary>

| Name                                                  | Required                                            | Description                                                                                                                            |
|-------------------------------------------------------|-----------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `edc.iam.sts.oauth.client.secret.alias`               | Defaults to `sts-client-secret`                     | Vault alias for the STS oauth client secret                                                                                            |
| `tx.edc.dataplane.token.refresh.endpoint`             | Defaults to `[publicApi]/token`                     | Path of the EDR Token Refresh Endpoint. Required config for EDR V3 Token Refreshing to work                                            |
| `tx.edc.dpf.consumer.proxy.port`                      | Defaults to value from `web.http.proxy.port`        | Set to same as web.http.proxy.port.                                                                                                    |
| `tx.edc.iam.iatp.default-scopes.governance.alias`     | Defaults to `org.eclipse.tractusx.vc.type`          | The alias of the scope 'governance'                                                                                                    |
| `tx.edc.iam.iatp.default-scopes.governance.operation` | Defaults to `read`                                  | The operation of the scope 'governance' e.g. 'read'                                                                                    |
| `tx.edc.iam.iatp.default-scopes.governance.type`      | Defaults to `DataExchangeGovernanceCredential`      | The credential type of the scope 'governance'                                                                                          |
| `tx.edc.iam.iatp.default-scopes.membership.alias`     | Defaults to `org.eclipse.tractusx.vc.type`          | The alias of the scope 'membership'                                                                                                    |
| `tx.edc.iam.iatp.default-scopes.membership.operation` | Defaults to `read`                                  | The operation of the scope 'membership' e.g. 'read'                                                                                    |
| `tx.edc.iam.iatp.default-scopes.membership.type`      | Defaults to `MembershipCredential`                  | The credential type of the scope 'membership'                                                                                          |
| `web.http.proxy.path`                                 | Defaults to `[basePath/]api/proxy`                  | API Group 'Consumer API / Proxy' contains private data plane API endpoints for quickly starting data exchanges. This is the base path. |
| `web.http.proxy.port`                                 | Defaults to value from `sovity.first.port` plus `6` | API Group 'Consumer API / Proxy' contains private data plane API endpoints for quickly starting data exchanges. This is the port.      |


</details>

