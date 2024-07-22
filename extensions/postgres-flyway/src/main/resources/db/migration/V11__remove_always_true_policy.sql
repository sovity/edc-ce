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

-- Convert JSON to JSONB to be able to use jsonb_set
alter table edc_policydefinitions
    alter column permissions type jsonb using permissions::jsonb;

update edc_policydefinitions
set permissions = jsonb_set(
    permissions::jsonb,
    '{0,constraints}',
    '[]'::jsonb
                  )
where policy_id = 'always_true';

-- Convert JSONB back to JSON for compatibility
alter table edc_policydefinitions
    alter column permissions type json using permissions::json;
