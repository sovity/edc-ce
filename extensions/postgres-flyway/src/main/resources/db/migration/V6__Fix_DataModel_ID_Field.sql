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
--       sovity GmbH - SQL Script
--
--

create
    or replace function pg_temp.migrate_distribution(distribution jsonb) returns jsonb as
$$
declare
    data_standard      jsonb;
    data_standard_path text[];
begin
    data_standard_path := '{https://w3id.org/mobilitydcat-ap/mobilityDataStandard}';
    data_standard := distribution #> data_standard_path;

    if jsonb_typeof(data_standard) = 'object' then
        data_standard := pg_temp.migrate_mobility_data_standard(data_standard);
    elsif jsonb_typeof(data_standard) = 'array' then
        data_standard := (select jsonb_agg(pg_temp.migrate_mobility_data_standard(it))
                          from jsonb_array_elements(data_standard) as it);
    end if;

    return jsonb_set(distribution, data_standard_path, data_standard, true);
end;
$$ language plpgsql;


create
    or replace function pg_temp.migrate_mobility_data_standard(data_standard jsonb) returns jsonb as
$$
begin
    return pg_temp.remove_if_blank(data_standard, '{@id}');
end;
$$ language plpgsql;


create
    or replace function pg_temp.remove_if_blank(obj jsonb, path text[]) returns jsonb as
$$
declare
    value text;
begin
    value := obj #>> path;
    if value is null or trim(value) = '' then
        obj := obj #- path;
    end if;
    return obj;
end;
$$ language plpgsql;


update edc_asset_property
set property_value = pg_temp.migrate_distribution(property_value::jsonb)::text
where property_name = 'http://www.w3.org/ns/dcat#distribution';
