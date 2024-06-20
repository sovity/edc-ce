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

create table sovity_contract_cancellation
(
    id int primary key,
    contract_id varchar,
    reason varchar,
    detail varchar,
    CONSTRAINT agreement_fk FOREIGN KEY (contract_id)
    REFERENCES edc_contract_definitions(contract_definition_id)
);
