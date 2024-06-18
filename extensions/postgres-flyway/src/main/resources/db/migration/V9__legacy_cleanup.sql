-- Clear the old migration tables if they exist once we reach this point.
-- These tables don't exist with this new line of updates
-- but may exist in older databases what started their lifetime with the older migration lines.
drop table if exists flyway_schema_history_asset;
drop table if exists flyway_schema_history_contractdefinition;
drop table if exists flyway_schema_history_contractnegotiation;
drop table if exists flyway_schema_history_dataplaneinstance;
drop table if exists flyway_schema_history_default;
drop table if exists flyway_schema_history_policy;
drop table if exists flyway_schema_history_transferprocess;

