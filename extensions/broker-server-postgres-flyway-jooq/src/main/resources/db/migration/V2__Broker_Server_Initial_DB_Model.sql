create type connector_online_status as enum ('ONLINE', 'OFFLINE');

create table connector
(
    endpoint      varchar(512)             not null,
    connector_id  varchar(512)             not null,
    ids_id        varchar(512)             not null,
    title         varchar(512),
    description   text,
    last_update   timestamp with time zone,
    offline_since timestamp with time zone,
    created_at    timestamp with time zone not null,
    online_status connector_online_status  not null,

    PRIMARY KEY (endpoint)
);