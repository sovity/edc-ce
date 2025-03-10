> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.deployment.kind`=`control-plane-standalone`

Deploys only a Control Plane. Requires at least one additionally deployed data plane to become a fully functioning EDC Connector.

## Choice: `sovity.dataspace.kind`=`sovity-mock-iam`

Configures the connector with sovity dataspace features, but in demo mode, mocking all dataspace central component interaction and without Connector-to-Connector IAM

[Back](../README.md)

## Config

### Important Config

| Name                 | Required | Description                                                                                     |
|----------------------|----------|-------------------------------------------------------------------------------------------------|
| `edc.participant.id` | Required | Participant ID / Connector ID. Usually handed out by the dataspace operator for your connector. |


### Overrides / Ignore

<details><summary>Show / Hide (1)</summary>

| Name                     | Required                | Description                                                                       |
|--------------------------|-------------------------|-----------------------------------------------------------------------------------|
| `edc.agent.identity.key` | Defaults to `client_id` | OAuth2 / DAPS: Access token claim name that must coincide with the Participant ID |


</details>

