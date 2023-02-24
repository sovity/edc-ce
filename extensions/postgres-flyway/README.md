# Extension: postgres-flyway

Provides all functionalities to persist EDC data in a PostgreSQL database.

## Why does this extension exist?

This extension contains all edc-Extensions that are required to persist EDC data in a PostgreSQL
database. This includes the edc-stores for the following edc-types:

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
