Deployment Goal: Local Demo
========

> On how to deploy a productive connector with joining an existing Data Space, please refer
> to our [Productive Deployment Guide](../production-ce/README.md).

## Quick Start

To quickly start using our sovity EDC CE or MDS EDC CE, we offer a quick
start [docker-compose.yaml](docker-compose.yaml) file.

## Launching Two Connectors

```shell script
# Run with Bash from the root directory of the project

# Log-In to the Github Container Registry
docker login ghcr.io

# Start sovity EDC Connectors
docker compose up
```

## Quick Start: Default Configuration

The default configuration launches two local EDC Connectors with the following credentials:

|                     | First Connector                                                  | Second Connector                                                 |
|---------------------|------------------------------------------------------------------|:-----------------------------------------------------------------|
| Homepage            | http://localhost:11000                                           | http://localhost:22000                                           |
| Management Endpoint | http://localhost:11000/api/management                            | http://localhost:22000/api/management                            |
| Management API Key  | `SomeOtherApiKey`                                                | `SomeOtherApiKey`                                                |
| Connector Endpoint  | http://provider:11003/api/dsp<br>Requires Docker Compose Network | http://consumer:11003/api/dsp<br>Requires Docker Compose Network |

