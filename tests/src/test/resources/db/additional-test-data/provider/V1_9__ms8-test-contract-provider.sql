--
-- Data for Name: edc_asset; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_asset VALUES ('urn:artifact:first-asset:1.0', 1695207769374);
INSERT INTO public.edc_asset VALUES ('urn:artifact:second-asset', 1695207798635);


--
-- Data for Name: edc_asset_dataaddress; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_asset_dataaddress VALUES ('urn:artifact:first-asset:1.0', '{"baseUrl":"http://localhost:23001/api/test-backend/data-source","method":"GET","queryParams":"data=first-asset-data","type":"HttpData"}');
INSERT INTO public.edc_asset_dataaddress VALUES ('urn:artifact:second-asset', '{"baseUrl":"http://localhost:23001/api/test-backend/data-source","method":"GET","queryParams":"data=second-asset-data","type":"HttpData"}');


--
-- Data for Name: edc_asset_property; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:curatorOrganizationName', 'Example GmbH', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:originatorOrganization', 'Example GmbH', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'http://w3id.org/mds#transportMode', 'Rail', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:contenttype', 'text/plain', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:datasource:http:hints:proxyMethod', 'false', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:version', '1.0', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'http://w3id.org/mds#geoReferenceMethod', 'geo-ref', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:id', 'urn:artifact:first-asset:1.0', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'http://w3id.org/mds#dataModel', 'data-model', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'http://w3id.org/mds#dataSubcategory', 'Accidents', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:datasource:http:hints:proxyPath', 'false', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:datasource:http:hints:proxyQueryParams', 'false', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:language', 'https://w3id.org/idsa/code/EN', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:keywords', 'first, asset', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:name', 'First Asset', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:description', 'My First Asset', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:datasource:http:hints:proxyBody', 'false', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:endpointDocumentation', 'https://endpoint-documentation', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:publisher', 'https://publisher', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'http://w3id.org/mds#dataCategory', 'Traffic Information', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:originator', 'http://localhost:21003/api/v1/ids/data', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:standardLicense', 'https://standard-license', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:first-asset:1.0', 'asset:prop:usecase', 'my-use-case', 'java.lang.String');
INSERT INTO public.edc_asset_property VALUES ('urn:artifact:second-asset', 'asset:prop:id', 'urn:artifact:second-asset', 'java.lang.String');


--
-- Data for Name: edc_contract_agreement; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_contract_agreement VALUES ('first-cd:28356d13-7fac-4540-80f2-22972c975ecb', 'urn:connector:provider', 'urn:connector:consumer', 1695207988, 1695207986, 1726743986, 'urn:artifact:first-asset:1.0', '{"permissions":[{"edctype":"dataspaceconnector:permission","uid":null,"target":"urn:artifact:first-asset:1.0","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2023-08-31T22:00:00.000Z"},"operator":"GEQ"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"urn:artifact:first-asset:1.0","@type":{"@policytype":"set"}}');


--
-- Data for Name: edc_contract_definitions; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_contract_definitions VALUES ('first-cd', 'first-policy', 'first-policy', '{"criteria":[{"operandLeft":"asset:prop:id","operator":"in","operandRight":["urn:artifact:first-asset:1.0"]}]}', 1695207936442, 31536000);
INSERT INTO public.edc_contract_definitions VALUES ('second-cd', 'always-true', 'always-true', '{"criteria":[{"operandLeft":"asset:prop:id","operator":"in","operandRight":["urn:artifact:second-asset"]}]}', 1695207948854, 31536000);


--
-- Data for Name: edc_contract_negotiation; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_contract_negotiation VALUES ('34ad04cd-4ce0-485f-a12e-aee0e37a9f03', '793d9064-8466-466e-93d1-25c379942c0d', 'urn:connector:example-connector', 'http://localhost:23003/api/v1/ids/data', 'ids-multipart', 1, 1200, 1, 1695207988502, NULL, 'first-cd:28356d13-7fac-4540-80f2-22972c975ecb', '[{"id":"first-cd:95c164c0-2bdd-4c1e-82e2-bec36e0664a5","policy":{"permissions":[{"edctype":"dataspaceconnector:permission","uid":null,"target":"urn:artifact:first-asset:1.0","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2023-08-31T22:00:00.000Z"},"operator":"GEQ"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"urn:artifact:first-asset:1.0","@type":{"@policytype":"set"}},"asset":{"id":"urn:artifact:first-asset:1.0","createdAt":1695207769374,"properties":{"asset:prop:curatorOrganizationName":"Example GmbH","http://w3id.org/mds#transportMode":"Rail","asset:prop:contenttype":"text/plain","asset:prop:datasource:http:hints:proxyMethod":"false","asset:prop:version":"1.0","http://w3id.org/mds#geoReferenceMethod":"geo-ref","asset:prop:id":"urn:artifact:first-asset:1.0","http://w3id.org/mds#dataModel":"data-model","http://w3id.org/mds#dataSubcategory":"Accidents","asset:prop:datasource:http:hints:proxyPath":"false","asset:prop:datasource:http:hints:proxyQueryParams":"false","asset:prop:language":"https://w3id.org/idsa/code/EN","asset:prop:keywords":"first, asset","asset:prop:name":"first-asset","asset:prop:description":"My First Asset","asset:prop:datasource:http:hints:proxyBody":"false","asset:prop:endpointDocumentation":"https://endpoint-documentation","asset:prop:publisher":"https://publisher","http://w3id.org/mds#dataCategory":"Traffic Information","asset:prop:originator":"http://localhost:21003/api/v1/ids/data","asset:prop:standardLicense":"https://standard-license"}},"provider":"urn:connector:provider","consumer":"urn:connector:consumer","offerStart":null,"offerEnd":null,"contractStart":"2023-09-20T11:06:26.324Z","contractEnd":"2024-09-19T11:06:26.324Z"}]', '{}', NULL, 1695207987357, 1695207988502);

--
-- Data for Name: edc_data_plane_instance; Type: TABLE DATA; Schema: public; Owner: edc
--

--
-- Data for Name: edc_lease; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_lease VALUES ('example-connector', 1695208010863, 60000, '70080388-d8d0-4a0f-b22d-6dec3f9ec10b');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208071147, 60000, '1c6b5845-6d0a-481b-b7b3-9821cce8dbe4');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208131518, 60000, 'e919d926-86b6-4adf-82d2-000ff4680d4a');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208191767, 60000, 'bd7b7d52-860c-4411-840f-7d30ccd6ea82');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208252016, 60000, 'a41aac77-9573-41c3-b320-67d4e1211548');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208312268, 60000, 'ae7a5a2a-7032-4032-8852-a33f1dfbf564');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208372514, 60000, '72dfc775-d08b-4953-a12c-5ed2b6cb9969');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208432761, 60000, 'b7c53380-3f48-472b-baeb-4cb4c8eaa8d4');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208492995, 60000, 'bd5f7e8d-598e-4ef5-9419-dbc2247fb4f1');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208553244, 60000, '19c3ad84-ca37-49c4-a6b0-86c92f7eb0c3');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208613485, 60000, '757191a2-956a-4057-b149-1ab567924b90');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208673732, 60000, '79d3685c-7d03-4353-b3b2-7c4ca88a6330');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208734001, 60000, 'fd82bbaf-88c4-4b64-96b6-d5ea100015c9');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208794234, 60000, '822c4fd9-c786-4849-a7cd-9ce63fb2d6ee');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208854497, 60000, 'c053655a-28e5-4eea-bd43-483e442cccfe');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208914783, 60000, 'f66cab9c-fbb6-421b-b1a8-42cede6af7e2');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695208975029, 60000, 'ddaeef38-2bc9-43cd-a083-1cdb97e0a85a');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209035290, 60000, 'ad946d4d-0887-4b7a-9eb1-65c93aef5ec5');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209095539, 60000, '6333fcd4-a51d-4fcd-b449-13dfc348f295');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209155789, 60000, '6fc32793-0831-4e2c-b7ec-629834ec4b89');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209216047, 60000, '1f115d15-06be-4b69-b52f-e26ac4d1c923');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209276309, 60000, '978e21e8-ca45-4571-99e2-9e29e728a32c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209336562, 60000, '607f985e-e927-400f-8f8e-701661772c91');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209396829, 60000, '6e4a2083-8f49-4d38-a3a5-9652d8fee811');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209457069, 60000, '20acb8fe-79a2-4d63-b607-790de67ea918');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209517322, 60000, 'acb710ca-0600-4a30-b784-94530c155ce2');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209577577, 60000, 'aa0b2cdb-1bc7-43b7-8517-54c8ef1b5518');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209637822, 60000, '33475ffd-ab20-4064-80e0-03e710dbb7d5');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209698064, 60000, '88163bae-e57c-4863-89a9-5ce8862d412b');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209758311, 60000, '363f7604-c2ea-4211-8bc6-5a35bc66119a');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209818566, 60000, 'bb6ee8b3-7968-46c2-bf4a-b33b0b8aa623');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209878815, 60000, '6cd549c0-878f-4fe1-a365-b1621dbbb692');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209939072, 60000, 'd0d12685-1cfc-45ee-bb68-098c4870caff');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695209999345, 60000, 'c65e94e8-a2ba-436d-aaf1-c010a2bbf1d0');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210059598, 60000, '0114c5f9-0fb3-4e1d-bc14-1b3710ea831d');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210119851, 60000, 'e56fad21-5ca6-49dc-acb5-6c2a1cbab640');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210180119, 60000, 'dec31d6d-d0b7-433a-ae65-6abbf2a927ce');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210240372, 60000, '3a102ace-b301-4108-93f5-0a4650f459e1');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210300643, 60000, '477260f9-9285-44e0-af58-0fdcfb39f4e0');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210360926, 60000, '65abc39f-1d03-4f3d-8db4-32652a6df216');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210421168, 60000, 'a636d551-159a-40d7-82df-70e7b0c0ec14');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210481435, 60000, 'b8f86ea2-e7b6-4268-8c66-0be2bc5f7d75');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210541691, 60000, '95ec3530-6616-47bb-a0ce-01707d0ebd49');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210601976, 60000, '421d69df-8bda-4863-8ad5-f47593d28027');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210662248, 60000, 'e07dcbcd-36e4-4b83-a7d7-fa303ec4e5ca');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210722613, 60000, '446ba085-46f4-4f81-bdd1-6b37ce49ef45');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210782876, 60000, 'a0918ea0-8026-44d6-b04d-a1f1c65ff049');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210843144, 60000, '36f29613-de1e-4574-b12a-94a68747e1e5');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210903467, 60000, '358b182f-eca0-46f6-803c-1e7e0efc3f28');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695210963730, 60000, '497e1529-5b21-47bd-862b-02bf9e319dfa');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211023962, 60000, '0b137ded-6621-43e3-ad28-9fdae4e088bc');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211084241, 60000, '7b89cf3d-ee7b-46c4-a22d-0fc387585498');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211144486, 60000, 'da8f9b53-66bc-43d5-b12f-a02b70fa1812');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211204816, 60000, 'a0a521fc-22bc-4aae-91bf-49804e7cdcde');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211265137, 60000, '8eb86cfa-a831-4bd1-9848-1e032392b708');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211325446, 60000, 'f595da12-a76a-4a0b-810d-b3a4be024b8c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211385712, 60000, '6c9a3514-316a-4468-8ede-fbedc1d06ce5');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211446021, 60000, '9d48c8ae-17f3-4512-9007-8db2524f327f');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211506293, 60000, '913daf55-c450-4d7b-9bf9-20948b77856e');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211566554, 60000, '60373891-24a4-4d4e-bbf3-492996c1374c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211626827, 60000, '96413c12-d4fc-417b-8455-a9efb841920a');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211687553, 60000, '6e7b6ac2-17ae-4a02-b974-6da1c35c2250');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211748069, 60000, '2d5de73e-2c5c-4553-967f-18c8adcec211');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211808351, 60000, 'e67e3c12-f73a-4aed-8eef-45262b6b2f0a');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211868621, 60000, '1aad7ebc-5b0f-4020-a3f1-9bab43bd130f');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211928886, 60000, 'f955859d-f8b1-4d00-8330-10f8347452d7');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695211989171, 60000, '4c42e375-02e4-40aa-b4cc-671f20006a59');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212049439, 60000, '8a52fd3b-8c5b-4386-addc-7f32fa496f94');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212109700, 60000, '924c4be8-abd2-46d9-ba2e-91524809e686');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212169966, 60000, 'bbf29d09-33ae-4f6b-be1d-a47e0e5e0737');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212230241, 60000, '5aa44045-3642-4e5c-9711-4841b5681d92');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212290503, 60000, '470c4b7e-bfc9-4406-b275-f630c7350d33');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212350768, 60000, '09946130-38bc-44ee-b3d1-90e4323e065c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212411055, 60000, 'f0418e24-716a-4398-a5aa-1601a1b23bfb');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212471315, 60000, 'eeb470cf-4d46-48e4-8c00-f7f4e86e83c3');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212531573, 60000, 'f4d34f46-c97d-4b52-8449-841e4b1238bf');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212591829, 60000, 'be23a391-731f-4a7b-abd5-836e4f681747');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212652111, 60000, '2793ebaa-62c6-40f6-b260-a39e9d4eac76');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212712387, 60000, 'e17b5904-eba7-4cad-b71a-b541068f745f');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212772645, 60000, 'f51d2efa-dd23-4176-adeb-8d740b72bc41');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212832905, 60000, '8eb68ed4-3edd-4c0f-bbff-a9731e31413f');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212893185, 60000, '61875057-cd7b-47a1-9dbf-e0ade78ba62b');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695212953474, 60000, '474b6909-7cb6-4be0-835b-4d7e456b9ea1');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213013731, 60000, '9ad2c665-a6ac-45a1-aee9-a289dcd1fb73');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213073994, 60000, '241d0ca1-fd32-4da4-bd3b-f29b46f977e7');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213134260, 60000, '8f13d5d5-3538-461b-9292-cc34fc17c774');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213194524, 60000, '5fa04667-c51d-409c-9461-9b5491e3349a');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213254806, 60000, '92724334-01e5-4d83-8f58-8879b58433c7');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213315078, 60000, 'ec6b15c0-7fbf-4356-9dde-826f043aa7db');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213375377, 60000, '0d15ed6c-2172-4494-b9d0-49531733cfec');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213435657, 60000, 'bcfd1597-2224-45a3-9639-97cc5e93cfb2');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213495926, 60000, '644f1bbc-92d2-4821-943e-47f7d99ec35d');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213556200, 60000, '58f25b8f-1846-4187-818d-a68b86a6d720');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213616482, 60000, '0fb4ad31-06f7-41c2-8c67-3408a6ac77fb');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213676758, 60000, '79cdb279-35fd-4964-803b-62149549cb9c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213737020, 60000, '1e6b0aef-1df2-49c3-a5f8-78c53ae020aa');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213797290, 60000, '3ed0412b-038e-458c-ab90-98fa40afbe4d');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213857560, 60000, '831cf5f4-4140-456c-a14e-83e622a736ee');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213917822, 60000, '430b44e1-1712-4925-8ae9-87fddbeb412e');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695213978116, 60000, '764c7a26-82f3-456a-8a1c-cb1edb0d2207');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214038445, 60000, '18bb2a60-ac4e-4f5d-87e4-58e41759a4e1');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214098714, 60000, 'cf3a2416-f917-4af2-a5d5-d2c395861f76');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214158968, 60000, '1fba22ed-e697-492b-8f53-8b1617804248');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214219214, 60000, '51f5d170-f5ba-48a6-925b-606596fb15ca');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214279464, 60000, 'c509e43c-9103-4de0-a01c-bc529494002c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214339729, 60000, '083f74a1-8e9d-44f3-91b0-75c8c53afacd');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214399970, 60000, '3dbabac2-432e-42be-9cce-d2762d4eef50');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214460219, 60000, '4608d517-b691-474e-94e6-ec0102f839b6');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214520473, 60000, '030422b6-6574-40da-a672-d41d6d7d701e');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214580726, 60000, '11d37a34-ef21-4698-8639-2a4dab72553a');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214640977, 60000, '97b0700f-00d0-443e-a9e5-0a855333e601');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214701227, 60000, '08115fc1-8714-41da-87c9-439d0b2af0b1');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214761493, 60000, 'f063fe96-d0c6-49d5-ad75-71da3b475849');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214821758, 60000, '72c944e7-9953-4a02-be26-6f95bc3fa357');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214882003, 60000, 'c8b0f0c3-62f5-4ee1-9ef3-670703be5bd6');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695214942297, 60000, 'a7d51b4b-fbfe-41ea-ac7a-b710a6d75897');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215002928, 60000, '0419b198-7165-4253-ba97-a20ebf2f96a8');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215063601, 60000, '86f773cd-0e43-430b-9105-f3641d464899');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215124190, 60000, 'c0171539-be72-479b-bb73-77b68649230b');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215184722, 60000, '1276a425-cff9-4e04-ba9f-26511cad528b');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215245255, 60000, '3b78d677-0a74-4f15-9d5c-18e662eb61da');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215305864, 60000, '1ce94378-a09d-4723-8644-8076e8cf8b8a');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215366459, 60000, '142c2de3-c62a-4bd3-96ea-8f9799d1eb63');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215427090, 60000, '76eff2f3-66ff-4e75-83c7-10943ab8cc06');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215487690, 60000, '9990a113-6ceb-40e6-8a2a-48ecd0a04400');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215548308, 60000, '8ea71d36-68fc-4367-b08a-a836ea0532df');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215608921, 60000, '2ddb3989-965b-48f7-b090-e714e6bc4c3d');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215669522, 60000, 'f32194d1-13e2-4581-bba8-a5dd86a42004');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215730125, 60000, 'babb1962-faf0-44d1-9b91-34e1dfbe3677');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215790760, 60000, '829f65ca-f175-4f4b-b1aa-cd028c13e49f');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215851394, 60000, 'd5ceeb0e-eaed-4c9b-b807-109c9b6547b1');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215912023, 60000, '5c5ea417-e788-4099-a699-19cd37b76945');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695215972644, 60000, '14e8a4f3-ab84-48b4-88e0-9549440fa95f');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216033259, 60000, '765ecafb-44cc-4a7c-a5ce-828b4919729b');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216093883, 60000, 'f0e33d91-6c6c-425b-a4b6-cba41590f029');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216154504, 60000, '87a59629-0dd7-4aec-bf80-2213de732711');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216215108, 60000, '850e8ab1-c76d-4c68-93ae-3b213cc7d031');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216275727, 60000, '2bcd7495-4da5-42d0-9a92-d9d32f755000');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216336373, 60000, 'de49c1f0-1e66-4980-98e4-23fd4c1e00cb');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216397016, 60000, 'b62d6656-6696-4bc6-ae71-4f95c66b8360');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216457640, 60000, '0389736b-b491-424f-bed2-671d4a96383c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216518281, 60000, '4c486f7f-ac6f-46f4-9672-c8ce5b132272');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216578911, 60000, '0142fa88-648e-4302-baf4-ba8fb081e291');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216639534, 60000, 'fb182422-9dce-4b36-9863-9b6673982b3c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216700144, 60000, '352ce55e-1393-45a7-8201-b0dee4c2541f');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216760746, 60000, '8e3ea30f-7359-4c4c-ae68-fc01dbc39697');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216821373, 60000, '04a317fa-8b93-4052-afb3-5c46d9a9e44d');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216882029, 60000, '9ead3d6a-204d-4bb9-b45d-a359c9f7a8af');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695216942664, 60000, '873aa530-8524-453c-af2f-282c6e67a7f9');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217003279, 60000, 'b92a3d76-24b7-4efb-b563-6709306176b3');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217063910, 60000, '2313bf50-5f64-4c45-8ba5-20041a30ddeb');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217124536, 60000, '4c4a3a55-0786-4abf-b7a9-97d3240b93e8');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217185157, 60000, '978a0783-a979-47d2-bf16-86795e32a1b9');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217245780, 60000, 'cf3a877b-79f9-4ab8-9030-8c965d750cba');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217306429, 60000, 'f30ebf08-fa35-4f26-95b4-1c9b1eecc3c9');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217367085, 60000, 'bd53c6e9-0eee-4fe6-b833-ddaafe6098c3');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217427699, 60000, 'f4fee769-85c5-4db1-a204-c8db6e0f916f');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217488306, 60000, '3a118c7e-a1ed-4cef-bc26-af7a837a3b25');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217548918, 60000, '87107235-4a45-4811-9b03-3f8f8b2788cb');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217609620, 60000, 'ce853721-a41c-4e28-a287-7a7f9541857b');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217670247, 60000, '4c327909-ecb3-49ad-9b43-7e283e694dec');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217730850, 60000, 'adc0bb07-2664-4493-9767-f52ccfc3e4a6');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217791252, 60000, '3f417fe6-2854-45b9-935f-7de79b64c1bf');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217851511, 60000, 'd66dfda4-aaef-48dc-a1ec-73ceee38ab26');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217911769, 60000, '2e5431bc-40be-4c0a-98df-c43d85e828a2');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695217972022, 60000, '53d8bfa4-e204-412e-b715-9eeed541e1b7');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218032277, 60000, '8e0dc6af-e2b8-4f74-9426-3f52428edabf');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218092532, 60000, 'e99c185e-2f8d-4515-9b3f-fdf5b7211ea5');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218152828, 60000, '52993024-bf3d-4093-897b-c9995a8ffcb6');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218213088, 60000, 'e6e57f51-7bc9-46ab-a1f2-864c2fd73404');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218273333, 60000, 'cb785654-4760-4167-942c-eee463572240');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218333581, 60000, '628b6548-5287-4a33-8b65-53af36c8a153');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218393824, 60000, '93f8f29e-449a-4d51-afa0-10cecfb6c06e');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218454073, 60000, 'd13e0440-b278-40ba-b49f-414476e68340');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218514355, 60000, '289deb73-d591-412f-b478-e2d9d9b9484c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218574641, 60000, '46e7406a-272e-4673-974c-456e5b2cf8ca');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218634926, 60000, '8aafe444-64b0-4191-b44f-8732737bcdda');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218695204, 60000, '832db41b-5574-473b-8165-bbe92bff7d2a');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218755490, 60000, '61289f09-28c1-4078-b418-4490f93a12a4');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218815763, 60000, '957e10b5-e721-41bd-a16c-a9fa37adc965');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218876047, 60000, 'c403947a-b0ba-4e2b-a047-1cb20f5d8d92');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218936301, 60000, '07a6e41f-636b-4d03-b3e8-edf832dfa6f5');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695218996571, 60000, '2192254a-50f1-4bbb-ae78-62f8c4afe7be');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219056849, 60000, '020136b3-b44b-42e2-86db-c2f5659ea86e');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219117118, 60000, '2c1dd02e-3fa9-4079-abdc-db624332a0a9');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219177408, 60000, 'ccb3959e-fe5c-443c-8bde-d3dbe4b9fab3');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219237684, 60000, 'fdf217ac-a3a9-4103-a2c9-396c44bea4ca');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219297943, 60000, 'e3d73b2c-b259-4704-b75f-4f634280f224');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219358204, 60000, 'de9b2b16-fb62-442b-a749-318660449f5c');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219418458, 60000, '3c32b9fc-41c3-4542-8b64-81ee15d98738');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219478721, 60000, 'cefe9a7e-bf4d-4c35-9c76-e0cbdc336689');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219538990, 60000, '82457869-79d2-4772-b364-1141a6ef984b');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219599252, 60000, 'ba1e9e62-e3db-41c7-9af6-590dd27e7182');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219659526, 60000, '037352ac-030e-4a69-91a7-3dfcf0f7e433');
INSERT INTO public.edc_lease VALUES ('example-connector', 1695219719803, 60000, '34ab855d-4f66-4b9f-95f7-a79cd9f10bf0');


--
-- Data for Name: edc_policydefinitions; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_policydefinitions VALUES ('always-true', '[{"edctype":"dataspaceconnector:permission","uid":null,"target":null,"action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"ALWAYS_TRUE"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"true"},"operator":"EQ"}],"duties":[]}]', '[]', '[]', '{}', NULL, NULL, NULL, NULL, '{"@policytype":"set"}', 1695137823306);
INSERT INTO public.edc_policydefinitions VALUES ('first-policy', '[{"edctype":"dataspaceconnector:permission","uid":null,"target":null,"action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2023-08-31T22:00:00.000Z"},"operator":"GEQ"}],"duties":[]}]', '[]', '[]', '{}', NULL, NULL, NULL, NULL, '{"@policytype":"set"}', 1695207922457);


--
-- Data for Name: edc_transfer_process; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_transfer_process VALUES ('27075fc4-b18f-44e1-8bde-a9f62817dab2', 'PROVIDER', 600, 1, 1695208010855, 1695208010083, '{}', NULL, '{"definitions":[]}', NULL, '{"properties":{"baseUrl":"http://localhost:23001/api/test-backend/data-source","method":"GET","queryParams":"data=first-asset-data","type":"HttpData"}}', '[]', '34ab855d-4f66-4b9f-95f7-a79cd9f10bf0', 1695208010855, '{}');


--
-- Data for Name: edc_data_request; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_data_request VALUES ('f9a60bc8-0cb5-4f30-8604-2e3b1d020541', '27075fc4-b18f-44e1-8bde-a9f62817dab2', 'http://localhost:23003/api/v1/ids/data', 'ids-multipart', 'urn:connector:example-connector', 'urn:artifact:first-asset:1.0', 'first-cd:28356d13-7fac-4540-80f2-22972c975ecb', '{"properties":{"baseUrl":"https://webhook.site/6d5008a7-8c29-4e14-83c1-cc64f86d5398","method":"POST","type":"HttpData"}}', true, '{}', '{"contentType":"application/octet-stream","isFinite":true}', '27075fc4-b18f-44e1-8bde-a9f62817dab2');
