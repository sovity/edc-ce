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
    id  text;
    tmp jsonb;
begin
    id := distribution -> 'https://w3id.org/mobilitydcat-ap/mobilityDataStandard' ->> '@id';
    tmp := distribution #- '{https://w3id.org/mobilitydcat-ap/mobilityDataStandard,@id}';
    tmp := case
               when id = '' or id is null then tmp
               else jsonb_set(tmp,
                              '{https://w3id.org/mobilitydcat-ap/mobilityDataStandard,https://w3id.org/mobilitydcat-ap/mobility-data-standard}',
                              to_jsonb(id),
                              true)
        end;
    return tmp;
end;
$$ language plpgsql;

-- for an unknown reason setting a Data Model in 7.4.0 resulted in the @id field being wrapped in an array
-- because of that we migrate the @id to a normal field
update edc_asset_property
set property_value = pg_temp.migrate_distribution_value(property_value::jsonb)::text
where property_name = 'http://www.w3.org/ns/dcat#distribution';
