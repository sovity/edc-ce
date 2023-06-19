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

-- Contract Negotiations
alter table edc_contract_negotiation drop constraint provider_correlation_id;
alter table edc_contract_negotiation alter column "type" type varchar using '';
alter table edc_contract_negotiation add column callback_addresses json;

-- Contract Definitions
alter table edc_contract_definitions rename column selector_expression to assets_selector;

-- Transfer Processes
alter table edc_transfer_process rename column transferprocess_properties to private_properties;
alter table edc_transfer_process drop column if exists transfer_type;
alter table edc_transfer_process add column callback_addresses json;
