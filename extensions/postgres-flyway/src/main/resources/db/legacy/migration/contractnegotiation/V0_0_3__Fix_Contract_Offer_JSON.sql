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

UPDATE edc_contract_negotiation
SET contract_offers = co.contract_offers_edited
FROM (
    SELECT
        cn.id,
        jsonb_agg(
            jsonb_set(
                jsonb_set(
                    elems,
                    '{contractStart}',
                    to_json(to_char(to_timestamp(created_at/1000) AT TIME ZONE 'UTC', 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')::text)::jsonb
                ),
                '{contractEnd}',
                to_json(to_char(to_timestamp((created_at/1000) + 60 * 60 * 24 * 365) AT TIME ZONE 'UTC', 'YYYY-MM-DD"T"HH24:MI:SS.MS"Z"')::text)::jsonb
            )
        )::json as contract_offers_edited
    FROM
        edc_contract_negotiation cn,
        jsonb_array_elements(cn.contract_offers::jsonb) elems
    GROUP BY cn.id
) co
WHERE edc_contract_negotiation.id = co.id;

