create collation if not exists alphanumeric_with_natural_sort (provider = icu, locale = 'en-u-kn-true');

create type connector_data_offers_exceeded as enum ('OK', 'EXCEEDED');
create type connector_contract_offers_exceeded as enum ('OK', 'EXCEEDED');

alter table broker_event_log
    drop column duration_in_ms;

alter table connector
    alter column endpoint type text collate alphanumeric_with_natural_sort,
    add column data_offers_exceeded     connector_data_offers_exceeded,
    add column contract_offers_exceeded connector_contract_offers_exceeded;

update connector
set data_offers_exceeded     = 'OK',
    contract_offers_exceeded = 'OK';

alter table connector
    alter column data_offers_exceeded set not null,
    alter column contract_offers_exceeded set not null;

alter table data_offer
    alter column asset_id type text collate alphanumeric_with_natural_sort,
    add column asset_name text collate alphanumeric_with_natural_sort;

update data_offer
set asset_name = coalesce(asset_properties ->> 'asset:prop:name', asset_id);

alter table data_offer
    alter column asset_name set not null;

-- update contract offer table's primary key
alter table data_offer_contract_offer
    drop constraint data_offer_contract_offer_pkey;
alter table data_offer_contract_offer
    add primary key (connector_endpoint, asset_id, contract_offer_id);
