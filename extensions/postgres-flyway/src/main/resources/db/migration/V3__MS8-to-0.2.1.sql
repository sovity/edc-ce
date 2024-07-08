--
--  Copyright (c) 2023 sovity GmbH
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       sovity GmbH - Update Tables From MS8 to 0.1.0 EDC
--
--

-- Migrates an Asset ID
create
    or replace function pg_temp.migrate_asset_id(asset_id text) returns text as
$$
begin
    return replace(replace(asset_id::text, 'urn:artifact:', ''), ':', '-');
end;
$$
    language plpgsql;

-- Migrate a contract agreement ID to EDC 0
create
    or replace function pg_temp.migrate_contract_agreement_id(contract_agreement_id text, asset_id text) returns text as
$$
begin
    return pg_temp.base64encode(split_part(contract_agreement_id, ':', 1)) || ':' ||
           pg_temp.base64encode(asset_id) || ':' ||
           pg_temp.base64encode(split_part(contract_agreement_id, ':', 2));
end;
$$
    language plpgsql;

-- Migrates a Connector Endpoint to EDC 0
create
    or replace function pg_temp.migrate_connector_endpoint(endpoint text) returns text as
$$
begin
    return pg_temp.replace_suffix(endpoint, '/api/v1/ids/data', '/api/dsp');
end;
$$
    language plpgsql;

-- Migrates a Participant ID to EDC 0
create
    or replace function pg_temp.migrate_participant_id(asset_id text) returns text as
$$
begin
    return replace(replace(asset_id::text, 'urn:connector:', ''), ':', '-');
end;
$$
    language plpgsql;

-- Migrates an Asset Property Name to EDC 0 (if possible)
create
    or replace function pg_temp.migrate_asset_property_name(asset_property_key text) returns text as
$$
begin
    return case asset_property_key
        -- This list only contains properties that are directly mappable
        -- Properties that require a new nested JSON structure are not included
               when 'asset:prop:id' then 'https://w3id.org/edc/v0.0.1/ns/id'
               when 'asset:prop:name' then 'http://purl.org/dc/terms/title'
               when 'asset:prop:language' then 'http://purl.org/dc/terms/language'
               when 'asset:prop:description' then 'http://purl.org/dc/terms/description'
               when 'asset:prop:standardLicense' then 'http://purl.org/dc/terms/license'
               when 'asset:prop:version' then 'http://www.w3.org/ns/dcat#version'
               when 'asset:prop:keywords' then 'http://www.w3.org/ns/dcat#keyword'
               when 'asset:prop:contenttype' then 'http://www.w3.org/ns/dcat#mediaType'
               when 'asset:prop:endpointDocumentation' then 'http://www.w3.org/ns/dcat#landingPage'
               when 'http://w3id.org/mds#dataCategory' then asset_property_key
               when 'http://w3id.org/mds#dataSubcategory' then asset_property_key
               when 'http://w3id.org/mds#dataModel' then asset_property_key
               when 'http://w3id.org/mds#geoReferenceMethod' then asset_property_key
               when 'http://w3id.org/mds#transportMode' then asset_property_key
               when 'asset:prop:datasource:http:hints:proxyMethod'
                   then 'https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyMethod'
               when 'asset:prop:datasource:http:hints:proxyPath'
                   then 'https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyPath'
               when 'asset:prop:datasource:http:hints:proxyQueryParams'
                   then 'https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyQueryParams'
               when 'asset:prop:datasource:http:hints:proxyBody'
                   then 'https://semantic.sovity.io/dcat-ext#httpDatasourceHintsProxyBody'
               else pg_temp.migrate_unknown_asset_property_name(asset_property_key)
        end;
end;
$$
    language plpgsql;

-- Migrates an unknown Asset Property Name to EDC 0 (if possible)
create
    or replace function pg_temp.migrate_unknown_asset_property_name(asset_property_key text) returns text as
$$
begin
    asset_property_key := replace(asset_property_key, 'asset:prop:', '');
    if pg_temp.starts_with(asset_property_key, 'http://') or
       pg_temp.starts_with(asset_property_key, 'https://') then
        return asset_property_key;
    end if;

    return 'http://unknown/' || replace(asset_property_key, ':', '-');
end;
$$
    language plpgsql;


-- Migrates the "keywords" property value to EDC 0
-- 'a, b'::text becomes '["a", "b"]'::text
create
    or replace function pg_temp.migrate_asset_keywords(keywords_comma_joined text) returns text as
$$
begin
    return (select coalesce(json_agg(to_json(trim(keyword)))::text, '[]')
            from unnest(regexp_split_to_array(keywords_comma_joined, ',')) as keyword
            where trim(keyword) <> '');
end;
$$
    language plpgsql;

-- Migrates an asset property to EDC 0
create
    or replace function pg_temp.migrate_asset_property(property_name text, property_value text, property_type text) returns jsonb as
$$
declare
    name_mapped  text;
    value_mapped text;
    type_mapped  text;
begin
    if property_name = 'asset:prop:id' then
        name_mapped = pg_temp.migrate_asset_property_name(property_name);
        value_mapped = pg_temp.migrate_asset_id(property_value);
        type_mapped = property_type;
    elsif property_name = 'asset:prop:keywords' then
        name_mapped = pg_temp.migrate_asset_property_name(property_name);
        value_mapped = pg_temp.migrate_asset_keywords(property_value);
        type_mapped = 'java.util.ArrayList';
    elsif property_name = 'asset:prop:publisher' then
        name_mapped = 'http://purl.org/dc/terms/publisher';
        value_mapped = jsonb_build_object('http://xmlns.com/foaf/0.1/homepage',
                                          pg_temp.jsonld_value(property_value))::text;
        type_mapped = 'java.util.LinkedHashMap';
    elsif property_name = 'asset:prop:curatorOrganizationName' or
          property_name = 'asset:prop:originatorOrganization' then
        name_mapped = 'http://purl.org/dc/terms/creator';
        value_mapped = jsonb_build_object('http://xmlns.com/foaf/0.1/name',
                                          pg_temp.jsonld_value(property_value))::text;
        type_mapped = 'java.util.LinkedHashMap';
    else
        name_mapped = pg_temp.migrate_asset_property_name(property_name);
        value_mapped = property_value;
        type_mapped = property_type;
    end if;

    return jsonb_build_object(
            'name', name_mapped,
            'value', value_mapped,
            'type', type_mapped
        );
end;
$$
    language plpgsql;

-- Migrates a contract definition criterion operator
create
    or replace function pg_temp.migrate_criterion_operator(op text) returns text as
$$
begin
    -- due to previous mixing of the criterion operator with ODRL operators, we need to ensure the data in the db
    -- is correct
    return case lower(op)
               when 'eq' then '='
               when 'in' then 'in'
               when 'like' then 'like'
               else op
        end;
end;
$$
    language plpgsql;

-- Migrates a contract definition criterion to EDC 0
create
    or replace function pg_temp.migrate_criterion(criterion jsonb) returns jsonb as
$$
declare
    operand_left_mapped  text;
    operator_mapped      text;
    operand_right_mapped jsonb;
begin
    operand_left_mapped = pg_temp.migrate_asset_property_name(criterion ->> 'operandLeft');
    operator_mapped = pg_temp.migrate_criterion_operator(criterion ->> 'operator');

    if criterion ->> 'operandLeft' = 'asset:prop:id' then
        if jsonb_typeof(criterion -> 'operandRight') = 'array' then
            operand_right_mapped = (select jsonb_agg(to_jsonb(pg_temp.migrate_asset_id(items.item)))
                                    from (select jsonb_array_elements_text(criterion -> 'operandRight') item) items);
        else
            operand_right_mapped = to_jsonb(pg_temp.migrate_asset_id(criterion ->> 'operandRight'));
        end if;
    else
        operand_right_mapped = criterion -> 'operandRight';
    end if;

    return jsonb_build_object(
            'operandLeft', operand_left_mapped,
            'operator', operator_mapped,
            'operandRight', operand_right_mapped
        );
end;
$$
    language plpgsql;

-- Migrates a contract offer JSON to EDC 0
create
    or replace function pg_temp.migrate_negotiation_contract_offer(contract_offer jsonb) returns jsonb as
$$
begin
    return (contract_offer - '{asset,provider,consumer,offerStart,offerEnd,contractStart,contractEnd}'::text[]
        || jsonb_build_object('assetId', pg_temp.migrate_asset_id(contract_offer -> 'asset' ->> 'id')));
end;
$$
    language plpgsql;

-- Migrates a JSON array of contract offers to EDC 0
create
    or replace function pg_temp.migrate_negotiation_contract_offers(contract_offers jsonb) returns jsonb as
$$
begin
    return (select jsonb_agg(pg_temp.migrate_negotiation_contract_offer(contract_offers.contract_offer))
            from (select jsonb_array_elements(contract_offers) contract_offer) contract_offers);
end;
$$
    language plpgsql;

-- Utility Function: Wraps a value in expanded JSON-LD
-- 'a'::text becomes '[{"@value": "a"}]'::jsonb
create
    or replace function pg_temp.jsonld_value(value text) returns jsonb as
$$
begin
    return jsonb_build_array(jsonb_build_object('@value', to_jsonb(value)));
end;
$$
    language plpgsql;

-- Utility Function: base64 encode
create
    or replace function pg_temp.base64encode(str text) returns text as
$$
begin
    return encode(str::bytea, 'base64');
end;
$$
    language plpgsql;

-- Utility Function: replaceSuffix
create
    or replace function pg_temp.replace_suffix(str text, old_suffix text, new_suffix text) returns text as
$$
begin
    return case
               when pg_temp.ends_with(str, old_suffix) then
                       left(str, length(str) - length(old_suffix)) || new_suffix
               else
                   str
        end;
end;
$$
    language plpgsql;

-- Utility Function: endsWith
create or replace function pg_temp.ends_with(str text, suffix text)
    returns boolean as
$$
begin
    return right(str, length(suffix)) = suffix;
end;
$$ language plpgsql;

-- Utility Function: startsWith
create or replace function pg_temp.starts_with(str text, prefix text)
    returns boolean as
$$
begin
    return left(str, length(prefix)) = prefix;
end;
$$ language plpgsql;

-- Asset IDs
alter table edc_asset_dataaddress
    drop constraint edc_asset_dataaddress_asset_id_fk_fkey;
alter table edc_asset_property
    drop constraint edc_asset_property_asset_id_fk_fkey;
update edc_asset
set asset_id = pg_temp.migrate_asset_id(asset_id);
update edc_asset_dataaddress
set asset_id_fk = pg_temp.migrate_asset_id(asset_id_fk);
update edc_asset_property
set asset_id_fk = pg_temp.migrate_asset_id(asset_id_fk);
update edc_contract_agreement
set asset_id = pg_temp.migrate_asset_id(asset_id);
update edc_data_request
set asset_id = pg_temp.migrate_asset_id(asset_id);
alter table edc_asset_dataaddress
    add constraint edc_asset_dataaddress_asset_id_fk_fkey foreign key (asset_id_fk) references edc_asset (asset_id) on delete cascade;
alter table edc_asset_property
    add constraint edc_asset_property_asset_id_fk_fkey foreign key (asset_id_fk) references edc_asset (asset_id) on delete cascade;

-- Contract Agreement IDs
alter table edc_contract_negotiation
    drop constraint contract_negotiation_contract_agreement_id_fk;
update edc_contract_negotiation
set agreement_id = pg_temp.migrate_contract_agreement_id(agreement_id,
                                                         pg_temp.migrate_asset_id(contract_offers -> 0 -> 'asset' ->> 'id'));
update edc_contract_agreement
set agr_id = pg_temp.migrate_contract_agreement_id(agr_id, asset_id);
update edc_data_request
set contract_id = pg_temp.migrate_contract_agreement_id(contract_id, asset_id);
alter table edc_contract_negotiation
    add constraint contract_negotiation_contract_agreement_id_fk foreign key (agreement_id) references edc_contract_agreement (agr_id);

-- Protocol
update edc_contract_negotiation
set protocol = 'dataspace-protocol-http';

-- Connector Endpoints
update edc_contract_negotiation
set counterparty_address = pg_temp.migrate_connector_endpoint(counterparty_address);
update edc_data_request
set connector_address = pg_temp.migrate_connector_endpoint(connector_address);


-- Participant IDs
update edc_data_request
set connector_id = pg_temp.migrate_participant_id(connector_id);
update edc_contract_agreement
set provider_agent_id = pg_temp.migrate_participant_id(provider_agent_id),
    consumer_agent_id = pg_temp.migrate_participant_id(consumer_agent_id);

-- Asset Properties
alter table edc_asset_property
    add column property_is_private boolean;
delete
from edc_asset_property legacy_prop
where legacy_prop.property_name = 'asset:prop:originator';
delete
from edc_asset_property legacy_prop
where legacy_prop.property_name = 'asset:prop:originatorOrganization'
  and exists(select 1
             from edc_asset_property newer_prop
             where legacy_prop.asset_id_fk = newer_prop.asset_id_fk
               and newer_prop.property_name = 'asset:prop:curatorOrganizationName'); -- prevents errors from merging the legacy properties
update edc_asset_property
set property_name  = pg_temp.migrate_asset_property(property_name, property_value, property_type) ->> 'name',
    property_value = pg_temp.migrate_asset_property(property_name, property_value, property_type) ->> 'value',
    property_type  = pg_temp.migrate_asset_property(property_name, property_value, property_type) ->> 'type';

-- Contract Negotiation Type
alter table edc_contract_negotiation
    drop constraint provider_correlation_id;
alter table edc_contract_negotiation
    add column "type_new" text;
update edc_contract_negotiation
set "type_new" = case "type" when 1 then 'PROVIDER' else 'CONSUMER' end;
alter table edc_contract_negotiation
    drop column "type";
alter table edc_contract_negotiation
    rename column "type_new" to "type";
alter table edc_contract_negotiation
    add constraint provider_correlation_id check (type = 'CONSUMER' OR correlation_id IS NOT NULL);

-- Contract Negotiation Contract Offers
update edc_contract_negotiation
set contract_offers = pg_temp.migrate_negotiation_contract_offers(contract_offers::jsonb)::json;

-- Contract Definitions Asset Selector
alter table edc_contract_definitions
    rename column selector_expression to assets_selector;
with cd_updated as (select cd.contract_definition_id,
                           jsonb_agg(pg_temp.migrate_criterion(criterion))::json as asset_selector
                    from edc_contract_definitions cd,
                         jsonb_array_elements(cd.assets_selector::jsonb -> 'criteria') criterion
                    group by cd.contract_definition_id)
update edc_contract_definitions cd
set assets_selector = cd_updated.asset_selector
from cd_updated
where cd_updated.contract_definition_id = cd.contract_definition_id;

-- Fix transfer processes stuck in running state
update edc_transfer_process
set state = 800
where state = 600;

-- Other DDL Changes
alter table edc_contract_negotiation
    add column callback_addresses json;
alter table edc_contract_negotiation
    add column pending boolean default false;
alter table edc_transfer_process
    add column pending boolean default false;
alter table edc_transfer_process
    add column callback_addresses json;

alter table edc_transfer_process
    rename column transferprocess_properties to private_properties;

alter table edc_contract_definitions
    drop column validity;
alter table edc_data_request
    drop column if exists managed_resources;
alter table edc_data_request
    drop column if exists properties;
alter table edc_data_request
    drop column if exists transfer_type;
