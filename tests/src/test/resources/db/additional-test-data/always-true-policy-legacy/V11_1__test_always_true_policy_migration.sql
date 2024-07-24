insert into edc.public.edc_policydefinitions
(policy_id, permissions, prohibitions, duties, extensible_properties, policy_type, created_at)
values ('always-true', '[
    {
        "edctype": "dataspaceconnector:permission",
        "target": null,
        "action": {
            "type": "USE",
            "includedIn": null,
            "constraint": null
        },
        "assignee": null,
        "assigner": null,
        "constraints": [
            {
                "edctype": "AtomicConstraint",
                "leftExpression": {
                    "edctype": "dataspaceconnector:literalexpression",
                    "value": "ALWAYS_TRUE"
                },
                "rightExpression": {
                    "edctype": "dataspaceconnector:literalexpression",
                    "value": "true"
                },
                "operator": "EQ"
            }
        ],
        "duties": []
    }
]',
        '[]',
        '[]',
        '{}',
        '{"@policytype":"set"}',
        1721740661)
