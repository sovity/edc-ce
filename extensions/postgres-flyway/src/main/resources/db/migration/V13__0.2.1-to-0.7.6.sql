--
--  Copyright (c) 2024 sovity GmbH
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       sovity GmbH - Improve database performance by removing duplicate indices
--

--- table: edc_edr_entry

create table edc_edr_entry
(
    transfer_process_id     varchar not null primary key,
    agreement_id            varchar not null,
    asset_id                varchar not null,
    provider_id             varchar not null,
    contract_negotiation_id varchar,
    created_at              bigint  not null
);

--- table: edc_asset

alter table edc_asset
    add column properties json default '{}';
comment on column edc_asset.properties IS 'Asset properties serialized as JSON';

alter table edc_asset
    add column private_properties json default '{}';
comment on column edc_asset.private_properties IS 'Asset private properties serialized as JSON';

alter table edc_asset
    add column data_address json default '{}';
comment on column edc_asset.data_address IS 'Asset DataAddress serialized as JSON';


--- table: edc_asset_dataaddress

update edc_asset
set data_address = da.properties
from edc_asset_dataaddress da
where edc_asset.asset_id = da.asset_id_fk;
drop table edc_asset_dataaddress;


--- table: edc_asset_property

with agg as (
    select
        asset_id_fk,
        json_agg(json_build_object(property_name, property_value))
        filter (where property_is_private = false or property_is_private is null) as public_properties,
        json_agg(json_build_object(property_name, property_value))
        filter (where property_is_private = true) as private_properties
    from edc_asset_property
    group by asset_id_fk
)
update edc_asset
set
    properties = coalesce(agg.public_properties, '{}'::json),
    private_properties = coalesce(agg.private_properties, '{}'::json)
from agg
where edc_asset.asset_id = agg.asset_id_fk;

drop table edc_asset_property;


--- table: edc_contract_definitions

alter table edc_contract_definitions
    add column private_properties json;


--- table: edc_contract_negotiation

alter table edc_contract_negotiation
    add column protocol_messages json;

alter table edc_contract_negotiation
    drop constraint provider_correlation_id;

-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index if not exists contract_negotiation_state on edc_contract_negotiation (state, state_timestamp);


--- table: edc_policydefinitions

alter table edc_policydefinitions
    add column private_properties json;


--- table: edc_transfer_process

alter table edc_transfer_process
    add column transfer_type varchar;

alter table edc_transfer_process
    add column protocol_messages json;

alter table edc_transfer_process
    add column data_plane_id varchar;

alter table edc_transfer_process
    add column correlation_id varchar;

alter table edc_transfer_process
    add column counter_party_address varchar;

alter table edc_transfer_process
    add column protocol varchar;

alter table edc_transfer_process
    add column asset_id varchar;

alter table edc_transfer_process
    add column contract_id varchar;

alter table edc_transfer_process
    add column data_destination json;

-- TODO
update edc_transfer_process
set transfer_type = 'TODO',
    protocol_messages = '"TODO"'::json,
    data_plane_id = 'TODO',
    correlation_id = 'TODO, might be sourced from ContractNegotiation',
    counter_party_address = dr.connector_address,
    protocol = dr.protocol,
    asset_id = dr.asset_id,
    contract_id = dr.contract_id,
    data_destination = dr.data_destination
from edc_data_request dr
where edc_transfer_process.transferprocess_id = dr.transfer_process_id;

drop table edc_data_request;

-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index if not exists transfer_process_state on edc_transfer_process (state, state_time_stamp);

-- table: edc_data_plane_instance

alter table edc_data_plane_instance
    add column lease_id varchar
        constraint data_plane_instance_lease_id_fk
            references edc_lease
            on delete set null;

-- table: edc_assesstokendata
-- TODO: Check, do we need this even?

create table edc_accesstokendata
(
    id           varchar not null primary key,
    claim_token  json    not null,
    data_address json    not null,
    additional_properties json default '{}'
);

comment on column edc_accesstokendata.claim_token is 'ClaimToken serialized as JSON map';
comment on column edc_accesstokendata.data_address is 'DataAddress serialized as JSON map';
comment on column edc_accesstokendata.additional_properties is 'Optional Additional properties serialized as JSON map';


-- table: edc_data_plane
-- TODO: Do we truly not need this?
--
-- alter table edc_data_plane
--     add column state_count integer default 0 not null;
--
-- alter table edc_data_plane
--     add column state_time_stamp bigint;
--
-- alter table edc_data_plane
--     add column trace_context json;
-- comment on column edc_data_plane.trace_context is 'Java Map serialized as JSON';
--
-- alter table edc_data_plane
--     add column error_detail varchar;
--
-- alter table edc_data_plane
--     add column callback_address varchar;
--
-- alter table edc_data_plane
--     add column lease_id varchar
--         constraint data_plane_lease_lease_id_fk
--             references edc_lease
--             on delete set null;
--
-- alter table edc_data_plane
--     add column source json;
-- comment on column edc_data_plane.source is 'DataAddress serialized as JSON';
--
-- alter table edc_data_plane
--     add column destination json;
-- comment on column edc_data_plane.destination is 'DataAddress serialized as JSON';
--
-- alter table edc_data_plane
--     add column properties json;
-- comment on column edc_data_plane.properties is 'Java Map serialized as JSON';
--
-- alter table edc_data_plane
--     add column flow_type varchar;
--
-- -- This will help to identify states that need to be transitioned without a table scan when the entries grow
-- create index if not exists data_plane_state on edc_data_plane (state, state_time_stamp);

-- table: edc_policy_monitor

create table edc_policy_monitor
(
    entry_id             varchar not null primary key,
    state                integer not null,
    created_at           bigint  not null,
    updated_at           bigint  not null,
    state_count          integer default 0 not null,
    state_time_stamp     bigint,
    trace_context        json,
    error_detail         varchar,
    lease_id             varchar
        constraint policy_monitor_lease_lease_id_fk
                    references edc_lease
                    on delete set null,
    properties           json,
    contract_id          varchar
);

-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index if not exists policy_monitor_state on edc_policy_monitor (state, state_time_stamp);
