---
icon: code
---

Deployment Goal: Development
========

There is currently no way to launch running EDCs directly from our gradle projects.

## Launching the `docker-compose-dev.yaml`

To try out the latest snapshots of the EDC CE and EDC UI please run:

## Dev Docker Compose

To try out the latest **unstable** connector images with
the [docker-compose-dev.yaml](../../../../docker-compose-dev.yaml), execute:

<table>
<thead>
<tr>
<th>Launch two sovity EDC CE Connectors</th>
<th>Launch two MDS EDC CE Connectors</th>
</tr>
</thead>
<tbody>
<tr>
<td width="50%">

```shell script
# Run with Bash from the root directory of the project

# Log-In to the Github Container Registry
docker login ghcr.io

# Pull the latest images
docker compose --env-file .env.dev -f docker-compose-dev.yaml pull

# Start sovity EDC Connectors
docker compose --env-file .env.dev -f docker-compose-dev.yaml up
```

</td>
<td width="50%">

```shell script
# Run with Bash from the root directory of the project

# Log-In to the Github Container Registry
docker login ghcr.io

# Pull the latest images
docker compose --env-file .env.dev -f docker-compose-dev.yaml pull

# Start MDS EDC Connectors without activating the logging-house-extension
EDC_UI_ACTIVE_PROFILE=mds-open-source EDC_LOGGINGHOUSE_EXTENSION_ENABLED=false docker compose --env-file .env.dev -f docker-compose-dev.yaml up
```

</td>
</tr>
</tbody>
</table>

## Dev Docker Compose: Default Configuration

The default configuration launches two local EDC Connectors with the following credentials:

|                     | First Connector                                               | Second Connector                                               |
|---------------------|---------------------------------------------------------------|:---------------------------------------------------------------|
| Homepage            | http://localhost:11000                                        | http://localhost:22000                                         |
| Management Endpoint | http://localhost:11002/api/management                         | http://localhost:22002/api/management                          |
| Management API Key  | `ApiKeyDefaultValue`                                          | `ApiKeyDefaultValue`                                           |
| Connector Endpoint  | http://edc:11003/api/dsp <br> Requires Docker Compose Network | http://edc2:22003/api/dsp <br> Requires Docker Compose Network |
