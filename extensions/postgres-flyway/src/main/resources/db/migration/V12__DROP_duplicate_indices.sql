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
--       sovity GmbH - Improve database performance by removing duplicate indices
--

-- Drop the duplicate indexes if they exist for improved resource usage.
DROP INDEX IF EXISTS contract_agreement_id_uindex;
DROP INDEX IF EXISTS contract_negotiation_id_uindex;
DROP INDEX IF EXISTS data_request_id_uindex;
DROP INDEX IF EXISTS lease_lease_id_uindex;
DROP INDEX IF EXISTS edc_policydefinitions_id_uindex;
DROP INDEX IF EXISTS transfer_process_id_uindex;

