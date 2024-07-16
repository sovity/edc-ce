create collation if not exists alphanumeric_with_natural_sort (provider = icu, locale = 'en-u-kn-true');

-- Creating missing enums
create type connector_online_status as enum ('ONLINE', 'OFFLINE', 'DEAD');
create type connector_data_offers_exceeded as enum ('OK', 'EXCEEDED');
create type connector_contract_offers_exceeded as enum ('OK', 'EXCEEDED');
create type measurement_type as enum ('CONNECTOR_REFRESH');
create type measurement_error_status as enum ('ERROR', 'OK');
create type crawler_event_status as enum ('OK', 'ERROR');
create type crawler_event_type as enum (
    'CONNECTOR_UPDATED',
    'CONNECTOR_STATUS_CHANGE_ONLINE',
    'CONNECTOR_STATUS_CHANGE_OFFLINE',
    'CONNECTOR_STATUS_CHANGE_FORCE_DELETED',
    'CONTRACT_OFFER_UPDATED',
    'CONTRACT_OFFER_CLICK',
    'CONNECTOR_DATA_OFFER_LIMIT_EXCEEDED',
    'CONNECTOR_DATA_OFFER_LIMIT_OK',
    'CONNECTOR_CONTRACT_OFFER_LIMIT_EXCEEDED',
    'CONNECTOR_CONTRACT_OFFER_LIMIT_OK',
    'CONNECTOR_KILLED_DUE_TO_OFFLINE_FOR_TOO_LONG',
    'CONNECTOR_DELETED');

-- Adding missing columns from the crawler to the connector table
alter table connector
    add column last_refresh_attempt_at    timestamp with time zone,
    add column last_successful_refresh_at timestamp with time zone,
    add column online_status              connector_online_status            not null default 'OFFLINE',
    add column data_offers_exceeded       connector_data_offers_exceeded     not null default 'OK',
    add column contract_offers_exceeded   connector_contract_offers_exceeded not null default 'OK';

-- Data offers, additionally keyed by env ID
create table data_offer
(
    connector_id                  text                                        not null,
    asset_id                      text                                        not null,
    ui_asset_json                 jsonb                                       not null,
    created_at                    timestamp with time zone                    not null,
    updated_at                    timestamp with time zone,
    asset_title                   text collate alphanumeric_with_natural_sort not null,
    description_no_markdown       text                                        not null default ''::text,
    short_description_no_markdown text                                        not null default ''::text,
    data_category                 text                                        not null default ''::text,
    data_subcategory              text                                        not null default ''::text,
    data_model                    text                                        not null default ''::text,
    transport_mode                text                                        not null default ''::text,
    geo_reference_method          text                                        not null default ''::text,
    keywords                      text[]                                      not null default '{}'::text[],
    keywords_comma_joined         text                                        not null default ''::text,
    version                       text                                        not null default ''::text,
    primary key (connector_id, asset_id),
    constraint data_offer_connector_fkey foreign key (connector_id) references connector (connector_id)
);

-- Data offer Viewcount
create table data_offer_view_count
(
    id           serial primary key,
    connector_id text                     not null,
    asset_id     text                     not null,
    date         timestamp with time zone not null
);

create index data_offer_view_count_speedup on data_offer_view_count (connector_id, asset_id);

-- Contract offers, additionally keyed by env ID
create table contract_offer
(
    contract_offer_id text                     not null,
    connector_id      text                     not null,
    asset_id          text                     not null,
    ui_policy_json    jsonb                    not null,
    created_at        timestamp with time zone not null,
    updated_at        timestamp with time zone,
    primary key (contract_offer_id, connector_id, asset_id),
    constraint contract_offer_connector_fkey foreign key (connector_id) references connector (connector_id),
    constraint contract_offer_data_offer_fkey foreign key (connector_id, asset_id) references data_offer (connector_id, asset_id)
);

-- Event Log
create table crawler_event_log
(
    id           uuid                     not null primary key,
    environment  text                     not null,
    created_at   timestamp with time zone not null,
    user_message text                     not null,
    event        crawler_event_type       not null,
    event_status crawler_event_status     not null,
    connector_id text,
    asset_id     text,
    error_stack  text
);
create index crawler_event_log_speedup on crawler_event_log (environment, connector_id, asset_id, event_status);

-- Crawling exec time measurements
create table crawler_execution_time_measurement
(
    id             uuid                     not null primary key,
    environment    text                     not null,
    connector_id   text                     not null,
    created_at     timestamp with time zone not null,
    duration_in_ms bigint                   not null,
    type           measurement_type         not null,
    error_status   measurement_error_status not null
);

alter type component_type add value 'CATALOG_CRAWLER';
