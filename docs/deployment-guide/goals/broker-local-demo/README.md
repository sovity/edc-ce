# Deployment Goal: Broker Local Demo

> On how to deploy a productive Broker with joining an existing Data Space, please refer
> to our [Productive Deployment Guide](../broker-production/README.md).

## Quick Start

To quickly start using our sovity Broker, we offer a quick
start [docker-compose.yaml](../../../../docker-compose.yaml) file.

At release time it is pinned down to the release versions.

Mid-development it may be un-pinned back to the latest version.

### Launch the Broker and a Connector

```sh
# Run with Bash from the root directory of the project

# Log-In to the Github Container Registry
docker login ghcr.io

# Start sovity EDC Connectors
docker compose --file docker-compose.yaml up
```

|                     | Broker                                                           | Connectors                                                                                                       |
|---------------------|------------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|
| Homepage            | http://localhost:44000                                           | http://localhost:11000 <br> http://localhost:22000                                                               |
| Management Endpoint | http://localhost:44002/api/management                            | http://localhost:11002/api/management <br> http://localhost:22002/api/management                                 |
| Management API Key  | `ApiKeyDefaultValue`                                             | `ApiKeyDefaultValue`                                                                                             |
| Connector Endpoint  | http://broker:44003/api/dsp <br> Requires Docker Compose Network | http://connector:11003/api/dsp <br> http://connector:22003/api/dsp          <br> Requires Docker Compose Network |

<p align="right">(<a href="#readme-top">back to top</a>)</p>
