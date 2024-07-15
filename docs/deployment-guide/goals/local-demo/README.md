Deployment Goal: Local Demo
========

> On how to deploy a productive connector with joining an existing Data Space, please refer
> to our [Productive Deployment Guide](../production/README.md).

## Quick Start

To quickly start using our sovity EDC CE or MDS EDC CE, we offer a quick
start [docker-compose.yaml](../../../../docker-compose.yaml) file.

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

# Start sovity EDC Connectors
docker compose up
```

</td>
<td width="50%">

```shell script
# Run with Bash from the root directory of the project

# Log-In to the Github Container Registry
docker login ghcr.io

# Start MDS EDC Connectors
EDC_UI_ACTIVE_PROFILE=mds-open-source docker compose up
```

</td>
</tr>
</tbody>
</table>

## Quick Start: Default Configuration

The default configuration launches two local EDC Connectors with the following credentials:

|                     | First Connector                                               | Second Connector                                               |
|---------------------|---------------------------------------------------------------|:---------------------------------------------------------------|
| Homepage            | http://localhost:11000                                        | http://localhost:22000                                         |
| Management Endpoint | http://localhost:11002/api/management                         | http://localhost:22002/api/management                          |
| Management API Key  | `ApiKeyDefaultValue`                                          | `ApiKeyDefaultValue`                                           |
| Connector Endpoint  | http://edc:11003/api/dsp <br> Requires Docker Compose Network | http://edc2:11003/api/dsp <br> Requires Docker Compose Network |

<p align="right">(<a href="#readme-top">back to top</a>)</p>
