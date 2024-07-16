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
--       sovity GmbH - initial API and implementation
--

create type contract_terminated_by as enum ('SELF', 'COUNTERPARTY');

create table sovity_contract_termination
(
    contract_agreement_id varchar primary key,
    reason                varchar                  not null,
    detail                varchar                  not null,
    terminated_at         timestamp with time zone not null,
    terminated_by         contract_terminated_by   not null,
    constraint agreement_fk foreign key (contract_agreement_id)
        references edc_contract_agreement (agr_id)
);
