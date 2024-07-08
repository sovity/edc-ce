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

-- Migrates a Participant ID to EDC 0
create
    or replace function pg_temp.migrate_participant_id(asset_id text) returns text as
$$
begin
    return replace(replace(asset_id::text, 'urn:connector:', ''), ':', '-');
end;
$$
    language plpgsql;

-- Participant IDs
update edc_contract_negotiation
set counterparty_id = pg_temp.migrate_participant_id(counterparty_id);

update edc_contract_agreement
set provider_agent_id = neg.counterparty_id
from edc_contract_negotiation neg
where neg.agreement_id = edc_contract_agreement.agr_id
  and neg.type = 'CONSUMER';

update edc_contract_agreement
set consumer_agent_id = neg.counterparty_id
from edc_contract_negotiation neg
where neg.agreement_id = edc_contract_agreement.agr_id
  and neg.type = 'PROVIDER';

-- Optimizations for Transfer Processes
create index transfer_process_status
    on edc_transfer_process (state);

-- Fix transfer processes stuck in running state
update edc_transfer_process
set state = 800
where state = 600;
