/*
 * Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

-- sovity

create type contract_terminated_by as enum ('SELF', 'COUNTERPARTY');

create table sovity_contract_termination
(
    contract_agreement_id varchar primary key,
    reason                text                     not null,
    detail                text                     not null,
    terminated_at         timestamp with time zone not null,
    terminated_by         contract_terminated_by   not null,
    constraint agreement_fk foreign key (contract_agreement_id)
        references edc_contract_agreement (agr_id)
);

-- Drop the duplicate indexes if they exist for improved resource usage.
DROP INDEX IF EXISTS contract_agreement_id_uindex;
DROP INDEX IF EXISTS contract_negotiation_id_uindex;
DROP INDEX IF EXISTS data_request_id_uindex;
DROP INDEX IF EXISTS lease_lease_id_uindex;
DROP INDEX IF EXISTS edc_policydefinitions_id_uindex;
DROP INDEX IF EXISTS transfer_process_id_uindex;

-- This will mark the existing contracts as terminated and pending termination
-- The goal is to not use the old contracts anymore and inform the counterparty that it's been cancelled

alter table edc_contract_agreement
    add column sovity_marked_for_termination boolean default false;

-- Mark all the existing contracts to be terminated
update edc_contract_agreement set sovity_marked_for_termination = true;

