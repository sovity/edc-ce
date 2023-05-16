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

The extension includes the edc-stores for the following edc-types:

- asset
- contractdefinition
- contractnegotiation
- dataplaneinstance
- policy
- transferprocess

Futhermore, the `ConnectionsPool`, `transaction`-Extensions and the JDBC-Driver for the
PostgreSQL-Database are provided.

The tables are prepared using Flyway, which executes the .sql scripts included in
the `resources/migration` folder.

There are Sovity EDC Community Edition specific migration scripts in the folder `resources/migration/default`.

### Configuration

Additional Migration Scripts can be added by specifiying the configuration property
`edc.flyway.additional.migration.locations`. Values are comma separated and need to be correct [FlyWay migration
script locations](https://flywaydb.org/documentation/configuration/parameters/locations). These migration scripts need
to be compatible to the migrations in `resources/migration/default`.

For further configuration options, please refer to the configuration of our sovity Community Edition EDC and its .env
file.

## Why does this extension exist?

While the EDC is providing capabilities for individual persistence stores, our goal is to provide a single working
extension package to allow switching to a well-managed PostgreSQL persistence.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
