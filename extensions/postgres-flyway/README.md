<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />PostgreSQL + Flyway</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension Package

This extension bundles all functionalities for using the EDC with PostgreSQL persistence. It also includes the required
Flyway migrations and extensions.

### Details

This extensions includes the edc-stores for the following edc-types:

- asset
- contractdefinition
- contractnegotiation
- dataplaneinstance
- policy
- transferprocess

Futhermore the `ConnectionsPool`, `transaction`-Extensions and the JDBC-Driver for the
PostgreSQL-Database are provided.

The tables are prepared using Flyway, which executes the .sql scripts included in
the `resources/migration` folder.

## Why does this extension exist?

While the EDC is providing possibilities for individual persistence of stores, our goal is to provide a single working
extension package to allow switching to a well-managed PostgreSQL persistence at ease.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
