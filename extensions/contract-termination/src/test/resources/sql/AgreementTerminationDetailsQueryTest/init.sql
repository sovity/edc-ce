/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

-- first agreement ID

INSERT INTO public.edc_contract_agreement (agr_id, provider_agent_id, consumer_agent_id, signing_date, start_date,
                                           end_date, asset_id, policy)
VALUES ('ZGVmMQ==:YXNzZXQx:YTg4N2U4YmMtODBjZS00OWI2LTk2MWEtMWU3Njc0NmM5N2Fi', 'my-edc', 'my-edc2', 1719391733, null,
        null, 'asset1', '{
        "permissions": [
            {
                "edctype": "dataspaceconnector:permission",
                "target": "asset1",
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
        ],
        "prohibitions": [],
        "obligations": [],
        "extensibleProperties": {},
        "inheritsFrom": null,
        "assigner": null,
        "assignee": null,
        "target": "asset1",
        "@type": {
            "@policytype": "set"
        }
    }');

-- first agreement ID's negotiation

INSERT INTO public.edc_contract_negotiation (id, correlation_id, counterparty_id, counterparty_address, protocol, state,
                                             state_count, state_timestamp, error_detail, agreement_id,
                                             trace_context, lease_id, created_at, updated_at, type, callback_addresses,
                                             pending, contract_offers)
VALUES ('c5105748-08f1-4258-a42e-ea6ab7c88f9d', 'c5105748-08f1-4258-a42e-ea6ab7c88f9d', 'my-edc',
        'http://edc:11003/api/dsp', 'dataspace-protocol-http', 1200, 1, 1719391736017, null,
        'ZGVmMQ==:YXNzZXQx:YTg4N2U4YmMtODBjZS00OWI2LTk2MWEtMWU3Njc0NmM5N2Fi', '{}', null, 1719391732667, 1719391736017,
        'CONSUMER', '[]', false,
        '[
            {
                "id": "ZGVmMQ==:YXNzZXQx:MzVhN2M5OGYtOTYzYi00OTIwLTgyODAtYzIwMDI2MjMzZmI3",
                "policy": {
                    "permissions": [
                        {
                            "edctype": "dataspaceconnector:permission",
                            "target": "asset1",
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
                    ],
                    "prohibitions": [],
                    "obligations": [],
                    "extensibleProperties": {},
                    "inheritsFrom": null,
                    "assigner": null,
                    "assignee": null,
                    "target": "asset1",
                    "@type": {
                        "@policytype": "set"
                    }
                },
                "assetId": "asset1"
            }
        ]');


-- second agreement ID

INSERT INTO public.edc_contract_agreement (agr_id, provider_agent_id, consumer_agent_id, signing_date, start_date,
                                           end_date, asset_id, policy)
VALUES ('Y29udHJhY3Q=:YXNzZXQtMS4yLjM=:NWM4M2MzNTYtZGVlYi00NjFkLTg1ZTUtODQ0YzgwMGEwMmVm', 'my-edc', 'my-edc2',
        1717770256, null, null, 'asset-1.2.3', '{
        "permissions": [
            {
                "edctype": "dataspaceconnector:permission",
                "target": "asset-1.2.3",
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
        ],
        "prohibitions": [],
        "obligations": [],
        "extensibleProperties": {},
        "inheritsFrom": null,
        "assigner": null,
        "assignee": null,
        "target": "asset-1.2.3",
        "@type": {
            "@policytype": "set"
        }
    }');

-- second agreement ID's negotiation

INSERT INTO public.edc_contract_negotiation (id, correlation_id, counterparty_id, counterparty_address, protocol, state,
                                             state_count, state_timestamp, error_detail, agreement_id, trace_context,
                                             lease_id, created_at, updated_at, type, callback_addresses, pending,
                                             contract_offers)
VALUES ('aed211a6-498b-4ca7-9b24-c009e7268b08', 'aed211a6-498b-4ca7-9b24-c009e7268b08', 'my-edc',
        'http://edc:11003/api/dsp', 'dataspace-protocol-http', 1200, 1, 1717770258445, null,
        'Y29udHJhY3Q=:YXNzZXQtMS4yLjM=:NWM4M2MzNTYtZGVlYi00NjFkLTg1ZTUtODQ0YzgwMGEwMmVm', '{}', null, 1717770255259,
        1717770258445, 'CONSUMER', '[]', false, '[
        {
            "id": "Y29udHJhY3Q=:YXNzZXQtMS4yLjM=:NGJlNmI3NzYtZDdiNS00MGUwLWE4ZjktZDhkODg2Y2Q2ZDhl",
            "policy": {
                "permissions": [
                    {
                        "edctype": "dataspaceconnector:permission",
                        "target": "asset-1.2.3",
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
                ],
                "prohibitions": [],
                "obligations": [],
                "extensibleProperties": {},
                "inheritsFrom": null,
                "assigner": null,
                "assignee": null,
                "target": "asset-1.2.3",
                "@type": {
                    "@policytype": "set"
                }
            },
            "assetId": "asset-1.2.3"
        }
    ]');

insert into public.sovity_contract_termination(contract_agreement_id, reason, detail, terminated_at, terminated_by)
values ('Y29udHJhY3Q=:YXNzZXQtMS4yLjM=:NWM4M2MzNTYtZGVlYi00NjFkLTg1ZTUtODQ0YzgwMGEwMmVm', 'User Termination',
        'Terminated because of good reasons', '2024-07-03T16:59:01.518000+00:00', 'SELF');
