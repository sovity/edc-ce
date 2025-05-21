> This documentation is auto-generated. Do not edit manually.

# sovity EDC CE Connector Configuration

This is an auto-generated documentation of all known configuration.

## Choice: `sovity.deployment.kind`=`data-plane-standalone`

Deploys only a Data Plane. Depends on a control plane.

## Choice: `!prod && !jdbcUrl`

Launches DB via Testcontainers

[Back](../README.md)

## Config

### Optional Config

| Name                                        | Required | Description                                                                                                                                                   |
|---------------------------------------------|----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `sovity.testcontainer.postgres.init.script` | Optional | The name of the init script to execute at the creation of the container.                                                                                      |
| `sovity.testcontainer.postgres.initdb.args` | Optional | The arguments to pass to the Testcontainers' PostgreSQL's POSTGRES_INITDB_ARGS environment variable. e.g.<br>-c shared_preload_libraries='pg_stat_statements' |


