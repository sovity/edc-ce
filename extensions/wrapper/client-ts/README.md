<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Clients:<br />TypeScript API Client</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this component

TypeScript Client Library to be imported and used in arbitrary applications like
frontends or NodeJS projects.

You can find our API Wrapper Project
[here](https://github.com/sovity/edc-extensions/tree/main/extensions/wrapper).

## How to install

Requires a NodeJS / NPM project.

```shell script
npm i --save @sovity.de/edc-client
```

## How to use

Configure your EDC Client and use endpoints of our API Wrapper Extension:

```typescript
const edcClient: EdcClient = buildEdcClient({
    managementApiUrl: 'http://localhost:11002/api/v1/management',
    managementApiKey: 'ApiKeyDefaultValue',
});

let kpiData: KpiResult = await edcClient.useCaseApi.kpiEndpoint();
```

A minimal example project using the typescript API client can be found
[here](https://github.com/sovity/edc-extensions/tree/main/extensions/wrapper/client-ts-example).

## License

Apache License 2.0 - see
[LICENSE](https://github.com/sovity/edc-extensions/blob/main/LICENSE)

## Contact

sovity GmbH - contact@sovity.de
