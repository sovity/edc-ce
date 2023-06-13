create type connector_online_status as enum ('ONLINE', 'OFFLINE');
create type measurement_type as enum ('CONNECTOR_REFRESH');
create type measurement_error_status as enum ('ERROR', 'OK');
create type connector_data_offers_exceeded as enum ('OK', 'EXCEEDED');
create type connector_contract_offers_exceeded as enum ('OK', 'EXCEEDED');

create table connector
(
    endpoint                   text                               not null,
    connector_id               text                               not null,
    created_at                 timestamp with time zone           not null,
    last_refresh_attempt_at    timestamp with time zone,
    last_successful_refresh_at timestamp with time zone,
    online_status              connector_online_status            not null,
    data_offers_exceeded       connector_data_offers_exceeded     not null,
    contract_offers_exceeded   connector_contract_offers_exceeded not null,

    PRIMARY KEY (endpoint)
);

create table data_offer
(
    connector_endpoint text                     not null,
    asset_id           text                     not null,
    asset_properties   jsonb                    not null,
    created_at         timestamp with time zone not null,
    updated_at         timestamp with time zone,

    PRIMARY KEY (connector_endpoint, asset_id),
    FOREIGN KEY (connector_endpoint) REFERENCES connector (endpoint)
);

create table data_offer_contract_offer
(
    contract_offer_id  text                     not null,
    connector_endpoint text                     not null,
    asset_id           text                     not null,
    policy             jsonb                    not null,
    created_at         timestamp with time zone not null,
    updated_at         timestamp with time zone,

    PRIMARY KEY (connector_endpoint, asset_id, contract_offer_id),
    FOREIGN KEY (connector_endpoint, asset_id) REFERENCES data_offer (connector_endpoint, asset_id),
    FOREIGN KEY (connector_endpoint) REFERENCES connector (endpoint)
);

create type broker_event_type as enum (
    --Connector was successfully updated, and changes were incorporated
    'CONNECTOR_UPDATED',

    --Connector went online
    'CONNECTOR_STATUS_CHANGE_ONLINE',

    --Connector went offline
    'CONNECTOR_STATUS_CHANGE_OFFLINE',

    --Connector was "force deleted"
    'CONNECTOR_STATUS_CHANGE_FORCE_DELETED',

    --Contract Offer was updated
    'CONTRACT_OFFER_UPDATED',

    --Contract Offer was clicked
    'CONTRACT_OFFER_CLICK',

    --Connector Data Offer Limit was exceeded
    'CONNECTOR_DATA_OFFER_LIMIT_EXCEEDED',

    --Connector Data Offer Limit was not exceeded
    'CONNECTOR_DATA_OFFER_LIMIT_OK',

    --Connector Contract Offer Limit was exceeded
    'CONNECTOR_CONTRACT_OFFER_LIMIT_EXCEEDED',

    --Connector Contract Offer Limit was not exceeded
    'CONNECTOR_CONTRACT_OFFER_LIMIT_OK'
);

create type broker_event_status as enum (
    -- Default
    'OK',

    -- Failures
    'ERROR'
);

create table broker_event_log
(
    id                 serial primary key,
    created_at         timestamp with time zone not null,
    user_message       text                     not null,
    event              broker_event_type        not null,
    event_status       broker_event_status      not null,
    connector_endpoint text,
    asset_id           text,
    error_stack        text
);

create table broker_execution_time_measurement
(
    id                 serial primary key,
    created_at         timestamp with time zone not null,
    connector_endpoint text                     not null,
    duration_in_ms     bigint                   not null,
    type               measurement_type         not null,
    error_status       measurement_error_status not null
);

create index speedup on broker_event_log (connector_endpoint, asset_id, event_status);
