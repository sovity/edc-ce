create type connector_online_status as enum ('ONLINE', 'OFFLINE');

create table connector
(
    endpoint                   text                     not null,
    connector_id               text                     not null,
    created_at                 timestamp with time zone not null,
    last_refresh_attempt_at    timestamp with time zone,
    last_successful_refresh_at timestamp with time zone,
    online_status              connector_online_status  not null,

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

    PRIMARY KEY (contract_offer_id),
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
    'CONTRACT_OFFER_CLICK'
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
    error_stack        text,
    duration_in_ms     bigint
);

create index speedup on broker_event_log (connector_endpoint, asset_id, event_status);
