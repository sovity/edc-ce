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
--       sovity GmbH - Update Tables From 0.2.0 to 0.2.1 EDC
--
--

alter table edc_contract_negotiation add column pending boolean default false;
alter table edc_transfer_process add column pending boolean default false;