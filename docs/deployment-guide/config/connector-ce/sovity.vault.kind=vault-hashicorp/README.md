> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.vault.kind`=`vault-hashicorp`

Hashicorp-specific implementation of the EDC vault

[Back](../README.md)

## Config

### Important Config

| Name                        | Required | Description                                  |
|-----------------------------|----------|----------------------------------------------|
| `edc.vault.hashicorp.token` | Required | The token used to access the Hashicorp Vault |
| `edc.vault.hashicorp.url`   | Required | The URL of the Vault                         |


### Optional Config

| Name                                                | Required                     | Description                                                                                                                                  |
|-----------------------------------------------------|------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| `edc.vault.hashicorp.api.health.check.path`         | Defaults to `/v1/sys/health` | The URL path of the vault's /health endpoint                                                                                                 |
| `edc.vault.hashicorp.api.secret.path`               | Defaults to `/v1/secret`     | The URL path of the vault's /secret endpoint                                                                                                 |
| `edc.vault.hashicorp.health.check.enabled`          | Defaults to `true`           | `boolean` Whether or not the vault health check is enabled.                                                                                  |
| `edc.vault.hashicorp.health.check.standby.ok`       | Defaults to `false`          | `boolean` Specifies if being a standby should still return the active status code instead of the standby status code                         |
| `edc.vault.hashicorp.token.renew-buffer`            | Defaults to `30`             | `long` The renew buffer of the Hashicorp Vault token in seconds                                                                              |
| `edc.vault.hashicorp.token.scheduled-renew-enabled` | Defaults to `true`           | `boolean` Whether the automatic token renewal process will be triggered or not. Should be disabled only for development and testing purposes |
| `edc.vault.hashicorp.token.ttl`                     | Defaults to `300`            | `long` The time-to-live (ttl) value of the Hashicorp Vault token in seconds                                                                  |


