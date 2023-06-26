--
-- PostgreSQL database dump
--

-- Dumped from database version 11.20
-- Dumped by pg_dump version 11.20

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: edc_asset; Type: TABLE DATA; Schema: public; Owner: edc
--



--
-- Data for Name: edc_asset_dataaddress; Type: TABLE DATA; Schema: public; Owner: edc
--



--
-- Data for Name: edc_asset_property; Type: TABLE DATA; Schema: public; Owner: edc
--



--
-- Data for Name: edc_contract_agreement; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_contract_agreement (agr_id, provider_agent_id, consumer_agent_id, signing_date, start_date, end_date, asset_id, policy) VALUES ('test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', 'urn:connector:provider', 'urn:connector:consumer', 1687179323, 1687179321, 1718715321, 'urn:artifact:test:1.0', '{"permissions":[{"edctype":"dataspaceconnector:permission","uid":null,"target":"urn:artifact:test:1.0","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2022-05-31T22:00:00.000Z"},"operator":"GEQ"},{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2030-06-30T22:00:00.000Z"},"operator":"LT"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"urn:artifact:test:1.0","@type":{"@policytype":"set"}}');


--
-- Data for Name: edc_contract_definitions; Type: TABLE DATA; Schema: public; Owner: edc
--



--
-- Data for Name: edc_lease; Type: TABLE DATA; Schema: public; Owner: edc
--



--
-- Data for Name: edc_contract_negotiation; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_contract_negotiation (id, correlation_id, counterparty_id, counterparty_address, protocol, type, state, state_count, state_timestamp, error_detail, agreement_id, contract_offers, trace_context, lease_id, created_at, updated_at) VALUES ('6ae57214-b6ea-42b0-adaa-ad64ce6c883c', NULL, 'my-connector', 'http://edc:11003/api/v1/ids/data', 'ids-multipart', 0, 1200, 1, 1687179323356, NULL, 'test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', '[{"id":"test-contract-definition:bda8b77d-07f0-4fcd-a941-4f068b71f574","policy":{"permissions":[{"edctype":"dataspaceconnector:permission","uid":null,"target":"urn:artifact:test:1.0","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2022-05-31T22:00:00.000Z"},"operator":"GEQ"},{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2030-06-30T22:00:00.000Z"},"operator":"LT"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"urn:artifact:test:1.0","@type":{"@policytype":"set"}},"asset":{"id":"urn:artifact:test:1.0","createdAt":1687179321833,"properties":{"asset:prop:id":"urn:artifact:test:1.0"}},"provider":"urn:connector:provider","consumer":"urn:connector:consumer","offerStart":null,"offerEnd":null,"contractStart":"2023-06-19T12:55:21.833454574Z","contractEnd":"2024-06-18T12:55:21.833454574Z"}]', '{}', NULL, 1687179321839, 1687179323356);


--
-- Data for Name: edc_data_plane_instance; Type: TABLE DATA; Schema: public; Owner: edc
--



--
-- Data for Name: edc_transfer_process; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_transfer_process (transferprocess_id, type, state, state_count, state_time_stamp, created_at, trace_context, error_detail, resource_manifest, provisioned_resource_set, content_data_address, deprovisioned_resources, lease_id, updated_at, transferprocess_properties) VALUES ('3499e4f0-1f8c-4ae6-8ca3-82812586b80f', 'CONSUMER', 800, 1, 1687179352248, 1687179350848, '{}', NULL, '{"definitions":[]}', NULL, NULL, '[]', NULL, 1687179352248, '{}');
INSERT INTO public.edc_transfer_process (transferprocess_id, type, state, state_count, state_time_stamp, created_at, trace_context, error_detail, resource_manifest, provisioned_resource_set, content_data_address, deprovisioned_resources, lease_id, updated_at, transferprocess_properties) VALUES ('3c7606fd-e3e3-453a-bc38-e2fd6293acc8', 'CONSUMER', 800, 1, 1687179374739, 1687179373229, '{}', NULL, '{"definitions":[]}', NULL, NULL, '[]', NULL, 1687179374739, '{}');
INSERT INTO public.edc_transfer_process (transferprocess_id, type, state, state_count, state_time_stamp, created_at, trace_context, error_detail, resource_manifest, provisioned_resource_set, content_data_address, deprovisioned_resources, lease_id, updated_at, transferprocess_properties) VALUES ('683098a6-0ed0-4b1a-bf19-f1b0dc67f682', 'CONSUMER', 800, 1, 1687179378080, 1687179375831, '{}', NULL, '{"definitions":[]}', NULL, NULL, '[]', NULL, 1687179378080, '{}');


--
-- Data for Name: edc_data_request; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_data_request (datarequest_id, process_id, connector_address, protocol, connector_id, asset_id, contract_id, data_destination, managed_resources, properties, transfer_type, transfer_process_id) VALUES ('e213fc10-f61a-4aa0-80f8-7c581651f543', '3499e4f0-1f8c-4ae6-8ca3-82812586b80f', 'http://edc:11003/api/v1/ids/data', 'ids-multipart', 'consumer', 'urn:artifact:test:1.0', 'test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', '{"properties":{"baseUrl":"https://webhook.site/e542f69e-ff0a-4771-af18-0900a399137a","method":"POST","type":"HttpData"}}', false, '{}', '{"contentType":"application/octet-stream","isFinite":true}', '3499e4f0-1f8c-4ae6-8ca3-82812586b80f');
INSERT INTO public.edc_data_request (datarequest_id, process_id, connector_address, protocol, connector_id, asset_id, contract_id, data_destination, managed_resources, properties, transfer_type, transfer_process_id) VALUES ('34b587d1-9a1a-4bc2-a499-24a0e6975b97', '3c7606fd-e3e3-453a-bc38-e2fd6293acc8', 'http://edc:11003/api/v1/ids/data', 'ids-multipart', 'consumer', 'urn:artifact:test:1.0', 'test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', '{"properties":{"baseUrl":"https://webhook.site/e542f69e-ff0a-4771-af18-0900a399137a","method":"POST","type":"HttpData"}}', false, '{}', '{"contentType":"application/octet-stream","isFinite":true}', '3c7606fd-e3e3-453a-bc38-e2fd6293acc8');
INSERT INTO public.edc_data_request (datarequest_id, process_id, connector_address, protocol, connector_id, asset_id, contract_id, data_destination, managed_resources, properties, transfer_type, transfer_process_id) VALUES ('5e4cb5c8-fc8e-4231-ba86-bc5dbf7b5015', '683098a6-0ed0-4b1a-bf19-f1b0dc67f682', 'http://edc:11003/api/v1/ids/data', 'ids-multipart', 'consumer', 'urn:artifact:test:1.0', 'test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', '{"properties":{"baseUrl":"https://webhook.site/e542f69e-ff0a-4771-af18-0900a399137a","method":"POST","type":"HttpData"}}', false, '{}', '{"contentType":"application/octet-stream","isFinite":true}', '683098a6-0ed0-4b1a-bf19-f1b0dc67f682');


--
-- Data for Name: edc_policydefinitions; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_policydefinitions (policy_id, permissions, prohibitions, duties, extensible_properties, inherits_from, assigner, assignee, target, policy_type, created_at) VALUES ('always-true', '[{"edctype":"dataspaceconnector:permission","uid":null,"target":null,"action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"ALWAYS_TRUE"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"true"},"operator":"EQ"}],"duties":[]}]', '[]', '[]', '{}', NULL, NULL, NULL, NULL, '{"@policytype":"set"}', 1687179132887);


--
-- PostgreSQL database dump complete
--

