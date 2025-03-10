<!--
    Copyright 2025 sovity GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    SPDX-License-Identifier: Apache-2.0

    Contributors:
        sovity - init and continued development
-->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Clients:<br />TypeScript API Client</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this component

TypeScript Client Library to be imported and used in arbitrary applications like
frontends or NodeJS projects.

You can find our API definitions
[here](https://github.com/sovity/edc-ce/tree/main/connector/ce/libs/api).

## How to install

Requires global fetch API (provided by Node.js 18+ or browser).

```shell script
npm i --save @sovity.de/edc-client
```

## How to use

Configure your EDC Client and use endpoints of our API Wrapper Extension:

### Example Using API Key Auth

```typescript
const edcClient: EdcClient = buildEdcClient({
    managementApiUrl: 'http://localhost:11002/api/management/v2',
    managementApiKey: 'ApiKeyDefaultValue',
});

let kpiData: KpiResult = await edcClient.useCaseApi.getKpis();
```

### Example Using OAuth2 Client Credentials

```typescript
const edcClient: EdcClient = buildEdcClient({
    managementApiUrl: 'http://localhost:11002/api/management/v2',
    clientCredentials: {
        tokenUrl: 'http://localhost:11002/token',
        clientId: '{{your-connector}}-app',
        clientSecret: '...',
    },
});

let kpiData: KpiResult = await edcClient.useCaseApi.getKpis();
```

## License

Apache License 2.0 - see
[LICENSE](https://github.com/sovity/edc-ce/blob/main/LICENSE)

## Contact

sovity GmbH - contact@sovity.de
