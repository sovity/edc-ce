create table data_offer_view_count (
    id                 serial                   primary key,
    connector_endpoint text                     not null,
    asset_id           text                     not null,
    date               timestamp with time zone not null
);

create index data_offer_view_count_speedup on data_offer_view_count (connector_endpoint, asset_id);

