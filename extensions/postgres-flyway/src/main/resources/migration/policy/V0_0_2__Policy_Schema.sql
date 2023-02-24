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
--       sovity GmbH - Update Tables to Milestone-7 EDC
--
--
ALTER TABLE edc_policydefinitions
    ADD created_at BIGINT;

UPDATE edc_policydefinitions SET created_at=EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000;

ALTER TABLE edc_policydefinitions
    ALTER COLUMN created_at SET NOT NULL;
