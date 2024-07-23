update edc_policydefinitions
set permissions = jsonb_set(
    permissions::jsonb,
    '{0,constraints}',
    '[
        {
            "edctype": "AtomicConstraint",
            "operator": "EQ",
            "leftExpression": {
                "value": "ALWAYS_TRUE",
                "edctype": "dataspaceconnector:literalexpression"
            },
            "rightExpression": {
                "value": "true",
                "edctype": "dataspaceconnector:literalexpression"
            }
        }
    ]'::jsonb)::json
where policy_id = 'always_true';
