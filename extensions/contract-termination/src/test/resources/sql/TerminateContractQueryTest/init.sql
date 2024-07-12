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
 *
 */

-- agreements

INSERT INTO public.edc_contract_agreement (agr_id, provider_agent_id, consumer_agent_id, signing_date, start_date, end_date, asset_id, policy) VALUES ('Y29udHJhY3QtMjIwMDA=:YXNzZXQtMjIwMDA=:NzYzYzkxODctZTQ3Yi00ODJjLTkxMjAtYTJkMTM1MzQ2YWVm', 'my-edc2', 'my-edc', 1720077427, null, null, 'asset-22000', '{"permissions":[{"edctype":"dataspaceconnector:permission","target":"asset-22000","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"ALWAYS_TRUE"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"true"},"operator":"EQ"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"asset-22000","@type":{"@policytype":"set"}}');

-- negotiations

INSERT INTO public.edc_contract_negotiation (id, correlation_id, counterparty_id, counterparty_address, protocol, state, state_count, state_timestamp, error_detail, agreement_id, contract_offers, trace_context, lease_id, created_at, updated_at, type, callback_addresses, pending) VALUES ('e1ed2a95-41ef-4eaf-884e-934f1966f2a3', 'e1ed2a95-41ef-4eaf-884e-934f1966f2a3', 'my-edc2', 'http://edc2:11003/api/dsp', 'dataspace-protocol-http', 1200, 1, 1720077429664, null, 'Y29udHJhY3QtMjIwMDA=:YXNzZXQtMjIwMDA=:NzYzYzkxODctZTQ3Yi00ODJjLTkxMjAtYTJkMTM1MzQ2YWVm', '[{"id":"Y29udHJhY3QtMjIwMDA=:YXNzZXQtMjIwMDA=:NjkxOTY5NGQtYmQ5ZC00NzkxLThlMGUtMGFmNjM3YzA4OThi","policy":{"permissions":[{"edctype":"dataspaceconnector:permission","target":"asset-22000","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"ALWAYS_TRUE"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"true"},"operator":"EQ"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"asset-22000","@type":{"@policytype":"set"}},"assetId":"asset-22000"}]', '{}', null, 1720077426574, 1720077429664, 'CONSUMER', '[]', false);

