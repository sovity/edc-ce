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

update edc_policydefinitions
set permissions = jsonb_set(
    permissions::jsonb,
    '{0,constraints}',
    '[]'::jsonb)::json
where policy_id = 'always_true';
