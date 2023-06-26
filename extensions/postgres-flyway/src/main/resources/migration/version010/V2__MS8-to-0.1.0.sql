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

-- Assets
alter table edc_asset_property add column property_is_private boolean;

-- Update Asset IDs
ALTER TABLE edc_asset_dataaddress DROP CONSTRAINT edc_asset_dataaddress_asset_id_fk_fkey;
ALTER TABLE edc_asset_property DROP CONSTRAINT edc_asset_property_asset_id_fk_fkey;
UPDATE edc_asset SET asset_id = REPLACE(REPLACE(asset_id, 'urn:artifact:', ''), ':', '-');
UPDATE edc_asset_dataaddress SET asset_id_fk = REPLACE(REPLACE(asset_id_fk, 'urn:artifact:', ''), ':', '-');
UPDATE edc_asset_property SET asset_id_fk = REPLACE(REPLACE(asset_id_fk, 'urn:artifact:', ''), ':', '-');
ALTER TABLE edc_asset_dataaddress ADD CONSTRAINT edc_asset_dataaddress_asset_id_fk_fkey FOREIGN KEY (asset_id_fk) REFERENCES edc_asset (asset_id) ON DELETE CASCADE;
ALTER TABLE edc_asset_property ADD CONSTRAINT edc_asset_property_asset_id_fk_fkey FOREIGN KEY (asset_id_fk) REFERENCES edc_asset (asset_id) ON DELETE CASCADE;

UPDATE edc_asset_property SET property_name = 'https://w3id.org/edc/v0.0.1/ns/id', property_value=asset_id_fk WHERE property_name = 'asset:prop:id';

-- Contract Negotiations
alter table edc_contract_negotiation drop constraint provider_correlation_id;
alter table edc_contract_negotiation alter column "type" type varchar using '';
alter table edc_contract_negotiation add column callback_addresses json;

-- Contract Definitions
alter table edc_contract_definitions rename column selector_expression to assets_selector;

-- remove criteria parent
UPDATE edc_contract_definitions
SET assets_selector = assets_selector->'criteria'
WHERE assets_selector::jsonb ? 'criteria';

-- fix asset_id_operands
create or replace function migrate_json(criteria_json_array_element jsonb) returns jsonb as
$$
declare
    fixed_operand_right_list jsonb;
    r                        record;
begin
    fixed_operand_right_list = jsonb_build_array();
    if jsonb_typeof(criteria_json_array_element -> 'operandRight') = 'array' then
        for r in (select jsonb_array_elements(criteria_json_array_element -> 'operandRight') operand_right_element)
            loop
                fixed_operand_right_list = fixed_operand_right_list ||
                                           (replace(
                                                   replace(
                                                           r.operand_right_element::text,
                                                           'urn:artifact:', ''),
                                                   ':', '-'))::jsonb;
            end loop;
        criteria_json_array_element = jsonb_set(
                criteria_json_array_element,
                '{operandLeft}',
                '"https://w3id.org/edc/v0.0.1/ns/id"'
            );
        return jsonb_set(
                criteria_json_array_element,
                '{operandRight}',
                fixed_operand_right_list,
                false);
    else
        return criteria_json_array_element;
    end if;
end;
$$ language plpgsql;

UPDATE edc_contract_definitions
SET assets_selector = co.criteria_edited
FROM (SELECT cd.contract_definition_id,
             jsonb_agg(
                     migrate_json(elems)
                 )::json as criteria_edited
      FROM edc_contract_definitions cd,
           jsonb_array_elements(cd.assets_selector::jsonb) elems
      WHERE elems ->> 'operandLeft' = 'asset:prop:id'
      GROUP BY cd.contract_definition_id) co
WHERE edc_contract_definitions.contract_definition_id = co.contract_definition_id;

drop function if exists migrate_json(criteria_json_array_element jsonb);

-- Transfer Processes
alter table edc_transfer_process rename column transferprocess_properties to private_properties;
alter table edc_transfer_process drop column if exists transfer_type;
alter table edc_transfer_process add column callback_addresses json;
