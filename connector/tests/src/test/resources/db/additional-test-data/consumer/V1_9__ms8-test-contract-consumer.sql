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

INSERT INTO public.edc_contract_agreement VALUES ('first-cd:28356d13-7fac-4540-80f2-22972c975ecb', 'urn:connector:provider', 'urn:connector:consumer', 1695207988, 1695207986, 1726743986, 'urn:artifact:first-asset:1.0', '{"permissions":[{"edctype":"dataspaceconnector:permission","uid":null,"target":"urn:artifact:first-asset:1.0","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2023-08-31T22:00:00.000Z"},"operator":"GEQ"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"urn:artifact:first-asset:1.0","@type":{"@policytype":"set"}}');


--
-- Data for Name: edc_contract_definitions; Type: TABLE DATA; Schema: public; Owner: edc
--



--
-- Data for Name: edc_contract_negotiation; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_contract_negotiation VALUES ('793d9064-8466-466e-93d1-25c379942c0d', NULL, 'urn:connector:example-provider', 'http://localhost:' ||
                                                                                                                  '21003/api/v1/ids/data', 'ids-multipart', 0, 1200, 1, 1695207988404, NULL, 'first-cd:28356d13-7fac-4540-80f2-22972c975ecb', '[{"id":"first-cd:95c164c0-2bdd-4c1e-82e2-bec36e0664a5","policy":{"permissions":[{"edctype":"dataspaceconnector:permission","uid":null,"target":"urn:artifact:first-asset:1.0","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2023-08-31T22:00:00.000Z"},"operator":"GEQ"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"urn:artifact:first-asset:1.0","@type":{"@policytype":"set"}},"asset":{"id":"urn:artifact:first-asset:1.0","createdAt":1695207986324,"properties":{"asset:prop:id":"urn:artifact:first-asset:1.0"}},"provider":"urn:connector:provider","consumer":"urn:connector:consumer","offerStart":null,"offerEnd":null,"contractStart":"2023-09-20T11:06:26.32476749Z","contractEnd":"2024-09-19T11:06:26.32476749Z"}]', '{}', NULL, 1695207986331, 1695207988404);


--
-- Data for Name: edc_data_plane_instance; Type: TABLE DATA; Schema: public; Owner: edc
--

--
-- Data for Name: edc_lease; Type: TABLE DATA; Schema: public; Owner: edc
--



--
-- Data for Name: edc_policydefinitions; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_policydefinitions VALUES ('always-true', '[{"edctype":"dataspaceconnector:permission","uid":null,"target":null,"action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"ALWAYS_TRUE"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"true"},"operator":"EQ"}],"duties":[]}]', '[]', '[]', '{}', NULL, NULL, NULL, NULL, '{"@policytype":"set"}', 1695137823418);


--
-- Data for Name: edc_transfer_process; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_transfer_process VALUES ('946aadd4-d4bf-47e9-8aea-c2279070e839', 'CONSUMER', 800, 1, 1695208011094, 1695208008652, '{}', NULL, '{"definitions":[]}', NULL, NULL, '[]', NULL, 1695208011094, '{}');

--
-- Data for Name: edc_data_request; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_data_request VALUES ('f9a60bc8-0cb5-4f30-8604-2e3b1d020541', '946aadd4-d4bf-47e9-8aea-c2279070e839', 'http://localhost:21003/api/v1/ids/data', 'ids-multipart', 'consumer', 'urn:artifact:first-asset:1.0', 'first-cd:28356d13-7fac-4540-80f2-22972c975ecb', '{"properties":{"baseUrl":"https://webhook.site/6d5008a7-8c29-4e14-83c1-cc64f86d5398","method":"POST","type":"HttpData"}}', false, '{}', '{"contentType":"application/octet-stream","isFinite":true}', '946aadd4-d4bf-47e9-8aea-c2279070e839');
