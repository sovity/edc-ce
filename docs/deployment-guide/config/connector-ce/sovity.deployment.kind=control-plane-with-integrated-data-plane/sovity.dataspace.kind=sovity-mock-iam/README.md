> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.deployment.kind`=`control-plane-with-integrated-data-plane`

Deploys an EDC Connector with both a Control Plane and an integrated Data Plane.

## Choice: `sovity.dataspace.kind`=`sovity-mock-iam`

Configures the connector with sovity dataspace features, but in demo mode, mocking all dataspace central component interaction and without Connector-to-Connector IAM

[Back](../README.md)

## Config

### Important Config

| Name                 | Required | Description                                                                                     |
|----------------------|----------|-------------------------------------------------------------------------------------------------|
| `edc.participant.id` | Required | Participant ID / Connector ID. Usually handed out by the dataspace operator for your connector. |


### Overrides / Ignore

> [!WARNING]
> The properties in this section are mentioned for completeness and are not supported.
> They are present here because we use them internally.
> Expect them to change without notice.

<details><summary>Show / Hide (3)</summary>

| Name                                           | Required                | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
|------------------------------------------------|-------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `edc.agent.identity.key`                       | Defaults to `client_id` | OAuth2 / DAPS: Access token claim name that must coincide with the Participant ID                                                                                                                                                                                                                                                                                                                                                                                                                           |
| `sovity.contract.termination.thread.pool_size` | Defaults to `10`        | The number of contracts messages that can be simultaneously processed                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| `sovity.edc.ui.features.add.SOVITY_POLICIES`   | Defaults to `true`      | Filled out wildcard property `sovity.edc.ui.features.add.*` with value `SOVITY_POLICIES`. Set to `true` to individually enable the given EDC UI Feature. Not all given available values are supported by the Community Edition.<br><br>Available values for the asterisk:<br> * `CONNECTOR_LIMITS`<br> * `OPEN_SOURCE_MARKETING`<br> * `EE_BASIC_MARKETING`<br> * `CATENA_POLICIES`<br> * `SOVITY_POLICIES`<br> * `SPHINX_POLICIES`<br> * `SPHINX_ASSET_METADATA`<br> * `BUSINESS_PARTNER_GROUP_MANAGEMENT` |


</details>

