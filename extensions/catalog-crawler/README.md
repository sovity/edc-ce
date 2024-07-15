<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Catalog Crawler</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

The catalog crawler is a deployment unit depending on an existing Authority Portal's database:

- It is a modified EDC connector with the task to crawl the other connector's public data offers.
- It periodically checks the Authority Portal's connector list for its environment.
- It crawls the given connectors in regular intervals.
- It writes the data offers and connector statuses back into the Authority Portal DB.
- Each Environment configured in the Authority Portal requires its own Catalog Crawler with credentials for that environment's DAPS.

## Why does this component exist?

The Authority Portal uses a non-EDC stack, and the EDC stack cannot handle multiple sources of authority at once.

With the `DB -> UI` part of the broker having been moved to the Authority Portal, only the `Catalog -> DB` part remains as the Catalog Crawler,
as it requires Connector-to-Connector IAM within the given Dataspace.

## Deployment

Please see the [Catalog Crawler Productive Deployment Guide](../../docs/deployment-guide/goals/catalog-crawler-production/README.md) for more information.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
