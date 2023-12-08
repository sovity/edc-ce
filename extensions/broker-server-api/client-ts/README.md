<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-broker-server-extension">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">Broker Server API TypeScript Client Library</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-broker-server-extension/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-broker-server-extension/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this component

TypeScript Client Library to access APIs of our Broker Server Backend.

## How to install

Requires a NodeJS / NPM project.

```shell script
npm i --save @sovity.de/broker-server-client
```

## How to use

Configure your Broker Server Client and use endpoints of our Broker Server API:

```typescript
import {
    BrokerServerClient,
    buildBrokerServerClient,
    CatalogPageResult
} from '@sovity.de/broker-server-client';

const brokerServerClient: BrokerServerClient = buildBrokerServerClient({
    managementApiUrl: 'http://localhost:11002/api/management',
    managementApiKey: 'ApiKeyDefaultValue',
});

let catalog: CatalogPageResult = await edcClient.brokerServerApi.catalogPage();
```

## License

Apache License 2.0 - see
[LICENSE](https://github.com/sovity/edc-broker-server-extension/blob/main/LICENSE)

## Contact

sovity GmbH - contact@sovity.de
