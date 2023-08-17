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

INSERT INTO public.edc_asset (asset_id, created_at) VALUES ('urn:artifact:ms8-asset:1.0', 1687179246852);


--
-- Data for Name: edc_asset_dataaddress; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_asset_dataaddress (asset_id_fk, properties) VALUES ('urn:artifact:ms8-asset:1.0', '{"baseUrl":"http://localhost:23001/api/mock-data-address/data-source?data=b130536e-0a51-4d2d-aa8c-7591d6ad5bc8","method":"GET","type":"HttpData"}');


--
-- Data for Name: edc_asset_property; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'asset:prop:curatorOrganizationName', 'Example GmbH', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'http://w3id.org/mds#transportMode', 'Road', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'asset:prop:contenttype', 'application/json', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'asset:prop:version', '1.0', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'http://w3id.org/mds#geoReferenceMethod', 'Geo reference method', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'asset:prop:id', 'urn:artifact:ms8-asset:1.0', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'http://w3id.org/mds#dataModel', 'Data Model', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'http://w3id.org/mds#dataSubcategory', 'Accidents', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'asset:prop:language', 'https://w3id.org/idsa/code/EN', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'asset:prop:keywords', 'keyword1, keyword2', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'asset:prop:name', 'test', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'asset:prop:description', 'My Asset Description', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'http://w3id.org/mds#dataCategory', 'Traffic Information', 'java.lang.String');
INSERT INTO public.edc_asset_property (asset_id_fk, property_name, property_value, property_type) VALUES ('urn:artifact:ms8-asset:1.0', 'asset:prop:originator', 'http://localhost:22003/api/v1/ids/data', 'java.lang.String');


--
-- Data for Name: edc_contract_agreement; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_contract_agreement (agr_id, provider_agent_id, consumer_agent_id, signing_date, start_date, end_date, asset_id, policy) VALUES ('test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', 'urn:connector:provider', 'urn:connector:consumer', 1687179323, 1687179321, 1718715321, 'urn:artifact:ms8-asset:1.0', '{"permissions":[{"edctype":"dataspaceconnector:permission","uid":null,"target":"urn:artifact:ms8-asset:1.0","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2022-05-31T22:00:00.000Z"},"operator":"GEQ"},{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2030-06-30T22:00:00.000Z"},"operator":"LT"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"urn:artifact:ms8-asset:1.0","@type":{"@policytype":"set"}}');


--
-- Data for Name: edc_contract_definitions; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_contract_definitions (contract_definition_id, access_policy_id, contract_policy_id, selector_expression, created_at, validity) VALUES ('test-contract-definition', 'test-policy', 'test-policy', '{"criteria":[{"operandLeft":"asset:prop:id","operator":"in","operandRight":["urn:artifact:ms8-asset:1.0"]}]}', 1687179311116, 31536000);


--
-- Data for Name: edc_lease; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179352120, 60000, 'cc05bbad-f14c-4535-a7d3-b325abdeb880');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179374498, 60000, '7dd7fcd3-1a16-45cd-adbb-cd9ee0c29179');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179377748, 60000, 'd84fdcbc-68e2-45e7-abfb-08a934bfc7bf');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179412585, 60000, 'e4090d26-e4ad-4904-a607-f09ac0ead113');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179434938, 60000, '6c827ac6-b2d9-421a-9fb7-2aee912f6201');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179438139, 60000, '61f5cbe0-3e5f-47e5-b380-6d38e837dd41');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179472614, 60000, 'a54e77c3-d4f5-4e1e-95d2-ecf20b35690b');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179495026, 60000, 'e74d448a-01c1-43a1-8a29-317c9df02f6f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179498197, 60000, 'dfbfa882-4a3f-40e9-877e-1df0eb5c7f52');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179533325, 60000, '75828490-2941-46f5-8212-6c426a50f3fc');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179555885, 60000, '53161b93-9908-4706-b700-8f7d71d7ec7f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179559131, 60000, '3b06890f-a8df-4e37-a7f7-2b5553bad295');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179594147, 60000, 'a9fc8e45-9fe6-4ae8-a8b5-02ec77cf604d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179616551, 60000, '09c682c7-9da5-4cdd-8302-130959c5cbfb');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179619718, 60000, 'e926b88d-10fc-4208-88ee-7d7515c99b5e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179654778, 60000, '209bbc07-342e-4e8d-92d0-5fbce1c84d77');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179677075, 60000, '7504587d-8285-4974-af25-a520b85ea898');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179680194, 60000, 'f3918081-7f1f-4950-aff9-d9ffb1307c10');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179715560, 60000, 'b5318c29-e20a-4b2c-a3e9-51a2d6832154');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179737964, 60000, 'ef69129c-7421-4f59-9acf-3e4a16ed43dd');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179741122, 60000, 'ebb79f3f-60e9-4d9f-99ec-23540152aa2d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179776223, 60000, '93da0650-2fcc-4b9b-b3e3-548b99f62ed2');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179798729, 60000, 'f9db359a-5c65-4819-aa10-bcf80197cfac');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179801954, 60000, 'af11e842-653f-439c-a087-aec85f52d200');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179837172, 60000, '65639e3b-a249-4744-8a33-43e3edb0f7da');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179859314, 60000, '019f5064-ca7f-4e81-ac04-feddfe1ac98f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179862523, 60000, '01eb0eed-ba15-49cd-ac0a-1a7f3246552b');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179897822, 60000, '29381279-a53b-4421-8451-5efbce0bf4af');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179920182, 60000, '8f9938fd-bb6d-4e81-b4a5-df7f1647b1f9');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179923415, 60000, '223b7c40-16f7-4f81-acb0-3b5167d29659');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179958133, 60000, '7f6cb370-e648-4a04-8edd-86becbefbfea');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179980534, 60000, 'f5f532f5-c864-4c1f-b85a-3b36ba6d7008');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687179983684, 60000, 'ba660240-15b9-48b0-94bf-3ea7fbb7f309');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180019074, 60000, '0fd58bb1-db83-4441-9534-e4d1fa92dfb2');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180040609, 60000, '8beae9c7-fff0-4c48-92f9-3d39315d67fc');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180043862, 60000, '9f3a47d8-e5d6-40b1-9fa5-1044e71bbec6');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180079144, 60000, 'e4a1a0f3-89e1-4efd-8333-6b153da040d7');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180101647, 60000, 'f4fcbbfd-5d8e-42c6-90be-01504e96207b');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180104810, 60000, '68de6bdc-f9aa-4740-adfc-6f512800e031');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180139966, 60000, 'ddd6c3be-8f7a-4cfa-a53e-08edbe9ee9f5');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180162424, 60000, '6c26804e-89b4-4bb9-a95f-ed0df36d46ce');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180165637, 60000, 'b93a1fb8-5f0e-4bc1-be13-cc47e6a08f6d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180200875, 60000, '5422709b-3ee9-44c8-9cfa-0786743324fc');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180223066, 60000, 'faab9989-9545-4ac1-917d-e5af6e1e8ed9');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180226243, 60000, '893ba877-118d-4743-8a6d-f49a00dc723a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180261393, 60000, '3c384b59-0de2-4e4d-ac09-0596f1018c36');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180283934, 60000, '3819508d-f9d6-4119-92ad-d0d242699f74');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180287024, 60000, '9fb0d809-96e9-4c90-8bb2-5fdcfbd5ff7e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180322044, 60000, '71288bb2-d807-40e3-9a54-24b963bf28d3');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180344513, 60000, 'f64e3905-3ffa-48aa-a475-54d15cd6d0af');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180347674, 60000, '4fea3add-22da-4b6d-b57d-d9566b88f234');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180382939, 60000, 'd331b297-74e3-4d9d-8f68-7852ee686686');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180405294, 60000, '5241855c-df67-4c2d-9df1-fed3fbe0f39a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180408544, 60000, '326305d5-bc29-4887-9b38-7e7a2efee0e8');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180443640, 60000, 'e2e17ec8-c121-4b9d-88c6-5405d3cf752c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180466104, 60000, '4bb5a85a-16c1-4c7b-8dfa-24949fdc5994');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180469364, 60000, '7673b3ff-4018-4008-8bb6-f5a661256b3f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180504209, 60000, 'a476eae2-e4b9-4f0d-8b1d-dccad5b0ac85');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180526375, 60000, 'e768c175-abc4-40f2-9545-72ecbb1bba7e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180529584, 60000, '3b7a35ca-8ec1-4404-af1a-e6a4a981b0d9');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180565005, 60000, '7bd28e19-7331-4c51-8f9a-dc7083e939ab');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180586758, 60000, 'b453ff25-d2c6-4982-b7dd-3cf41b6106e2');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180590096, 60000, 'b4b1c00f-06ac-486b-8db0-4ffe5e2469a0');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180625486, 60000, '9be3fe6b-764e-4005-bcc3-58cde79e7f5c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180647030, 60000, 'bdee315a-4e85-450b-91ef-83e2591060ca');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180650199, 60000, 'c9232ae4-f951-4308-93d8-10f52a6573a2');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180686536, 60000, '716d548c-29f9-4476-83aa-612e76740fd2');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180708099, 60000, '905d211c-dab3-4ac5-a4dc-454c8fc6a1ca');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180710230, 60000, '3649c9c0-7c85-41c0-86c7-977f86c21ae7');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180746644, 60000, 'c9d311d4-2fff-4af6-be74-022208085e82');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180768212, 60000, '0a8b39ad-12ae-41c8-b8c6-e0d941c6a5e3');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180770344, 60000, '53c97ca2-f986-42f3-b101-b15d363a89b3');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180807634, 60000, 'a029840e-3973-4b35-95ce-3817a4b8f5b8');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180828893, 60000, '6c409fd1-502f-4634-98ff-37e5aa2d9b0d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180831076, 60000, 'c69bd663-7f72-4efd-adcc-6a00b1be0b4d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180868087, 60000, 'f1a03536-8293-49c9-a1ec-9f5f8150e859');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180889435, 60000, '37928944-ef9d-4bfb-a52e-af6c6454d874');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180891555, 60000, '8c432e4a-f3c1-4a9a-b4e4-8515efa3744f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180928629, 60000, '6e75a714-0730-403a-a3b3-aae259585404');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180949884, 60000, '1fcb040d-23b2-415c-88e5-b64e37b160bb');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180951992, 60000, '967f1e32-4355-4b13-b069-acb35e9493a9');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687180989214, 60000, 'bc575250-b9c3-43d4-891d-e23705f1567c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181010604, 60000, '2f6c78f5-2aa1-4d3b-9759-b46d67e468c6');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181012754, 60000, '07acacbc-3a8e-49d2-bbd0-4bd044931403');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181050094, 60000, '43105eed-bbd2-4db4-8987-b25adbc56dba');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181071404, 60000, 'e03e451c-8311-4922-8ce6-25a0b945ee01');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181073514, 60000, '69e7c627-f4af-44b9-b73e-bc0974eab8a4');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181110850, 60000, 'ee669b0f-9cb2-4ec4-b4c2-dc938da466ef');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181131938, 60000, 'bbb95833-9b7d-40fc-a09e-d86963abe80d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181134045, 60000, 'ca21cba9-7983-46f6-a4c0-bf46643292c7');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181171637, 60000, '2753fada-69f9-4e1b-9512-a6d3335ae813');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181192091, 60000, '2c6e2c52-cc35-41d2-a9ae-8f35fa9adeb4');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181194276, 60000, '0190886c-2e97-4fc8-a23c-519706de6757');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181231744, 60000, '307df5b3-4cc6-4f51-9894-431185f7213a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181253077, 60000, '45e7eec2-782d-4fd1-8785-7693ca0c0ca3');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181255213, 60000, 'c3faae35-40be-447e-93e5-6497f433bf3d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181292404, 60000, 'c1d74e18-faaa-4a5a-bfe0-8ad91acffefd');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181313854, 60000, '7bdd595c-2e75-4883-bf32-058645ac3e6e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181316130, 60000, '3521e77f-dfc7-4e4d-95c4-70875ef28d53');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181352420, 60000, '968d8ee7-3467-4d13-9ab6-ea90762e298e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181374024, 60000, 'ade80af1-3475-4736-95e1-802077b33ae6');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181376234, 60000, 'db35b302-9b57-4a12-9325-1da7e9bc2c7a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181412474, 60000, 'a599d165-1ec0-4c35-9b1e-beae9bc13c75');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181434814, 60000, '08aa45c8-9558-4b59-b2c9-d92b70bc4b43');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181437003, 60000, '3ecb3c96-8dd1-48e1-bdf9-72dce3d04551');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181473392, 60000, '3281a8d3-2350-4789-ab70-fc330103efbc');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181495705, 60000, '03593af5-d8da-41c9-9288-cbee86de2c3f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181497882, 60000, 'b4776d26-7706-4802-b94a-9df8ead31cdb');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181534375, 60000, '7ba3193c-0421-4f2b-b632-8ea75dfb113a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181556094, 60000, '1de79c66-681a-4d28-ab87-b51034d222f4');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181558177, 60000, '1b206786-28cf-49c1-91af-f8cc47b956f0');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181594591, 60000, '98322b9d-6196-4db7-8217-29d61be89e19');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181616157, 60000, '8a1c71a1-1300-4dbd-994a-d165d80bfdc5');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181618363, 60000, 'b8d51f56-5bc9-4c3e-9551-413c17dcb214');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181654934, 60000, '46581dc9-3300-469f-83ce-7891da5f5efb');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181676335, 60000, '1089f76a-e508-422b-bcbf-9512e07dd8b2');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181678463, 60000, '4e5c2df5-0a52-4c16-93fb-b22a7b411d90');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181714955, 60000, '62108594-12d4-4071-a05b-6e861a0c4eb6');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181736647, 60000, 'f694ee5a-99d7-45f5-8063-76de9d6afea5');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181738751, 60000, 'a47bf45c-c83b-451f-9ccd-3f5634fd8a2c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181775364, 60000, 'e7e6089e-dfbf-4505-9253-4577717f9b76');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181796876, 60000, '1fbb5b0c-c0c4-4469-b5b0-03cb3252dc47');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181799091, 60000, '4146e90b-1524-4e6d-9fd4-43e13f5e7204');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181835414, 60000, 'f3a9b320-014a-4720-acb6-25715cec6b17');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181856924, 60000, 'c86c138a-b602-47fb-97fc-0b742ec16da4');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181860153, 60000, 'd304a163-b051-48e7-8f75-e845ca95c54b');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181895677, 60000, '800f444d-8a50-4065-9c7b-0885bf010da7');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181918023, 60000, '4ce94eea-4d18-436f-a834-d450b4bbc283');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181920175, 60000, 'f4ca2036-98ec-4885-bf30-a1fcb595b4e0');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181956408, 60000, '7c1f8eea-fe36-4931-9b6d-3ae162053c46');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181978516, 60000, '1e033b4d-ae82-4058-9f02-b70a564fd1f0');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687181980708, 60000, '2d09e2dd-490a-43af-845e-62edd7dddc8e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182016708, 60000, '684d6660-d93c-4653-adfc-832c12c83a7e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182039262, 60000, '0e946393-c191-4039-8ac0-6e42d40c72da');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182041384, 60000, 'efd8ae2a-ca14-41df-bee2-7f15aa9488a0');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182076894, 60000, '95de2fe7-8164-41c9-a180-25c85efa5acd');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182099343, 60000, '6601b27c-2b04-44f0-b560-1b07f2cc0e1d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182101544, 60000, '7b996473-3d74-4d66-9d58-db9ef4c4dc5e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182137711, 60000, '305c012b-3cc1-4386-9fcd-b97eaf9ebead');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182160312, 60000, '6bfce472-a5db-4e6b-86ae-3feb34f76567');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182162464, 60000, '8697149d-a476-4161-9a05-527320818e5a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182197801, 60000, '92f17be2-3878-4a32-a3f0-138249de4b96');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182220328, 60000, '4bec86d3-bb76-4810-b442-d7458034c93a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182222535, 60000, '3b8f9394-5a52-4db4-b301-b5a794f25ebe');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182258165, 60000, 'de8dc27d-cc7e-4afd-8155-04c1e5ccef77');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182280723, 60000, '01a3287e-8629-4bd7-bab8-7e9a53ac3f7a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182282912, 60000, '5634a87d-c942-4425-b20e-0761ead0c5fb');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182319180, 60000, 'e4b87f47-f820-41cc-8f65-90e26db071e7');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182341697, 60000, 'aafb44dc-f6d4-45b5-a7dc-e46f5ed678c2');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182343912, 60000, 'e4369e55-9142-4a2e-8600-c562a00d2a62');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182379505, 60000, '9acfb1cb-0a64-4650-a7a6-08b9bbe88b47');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182401988, 60000, '99891d67-8ecc-4553-9da9-bd21dc038f75');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182404188, 60000, 'dda72386-8c98-453c-bccd-956d60779be7');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182439676, 60000, 'c391dc7f-cef0-4198-8e86-8ac960da5b62');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182462236, 60000, 'ee005d92-7679-4c06-a48a-d6d8bb2e0bf9');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182464444, 60000, '71f70b6e-b316-4216-bca9-2fc414e7af0c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182499915, 60000, '447a3db4-875f-4093-bd85-68bf8d004c3c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182522319, 60000, 'e645f660-5d00-4f4d-9a1b-172e948b0c44');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182524474, 60000, '78217435-0163-445b-9418-8ba64b416e5f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182560006, 60000, '25a9189f-dec0-48e2-8ba5-24bf28ab8450');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182582519, 60000, 'a5b63cf8-14ed-46d8-8751-63c0bb6af098');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182584702, 60000, '9b921107-567e-4f60-8db1-4d3e4d735329');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182620076, 60000, '760fe7db-5224-4abb-b68e-70e65f6370b5');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182642625, 60000, '67d78294-52f4-499b-a852-4039324c727c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182644854, 60000, '952fda3a-ae3e-442c-b1d9-b846034fca2f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182680277, 60000, 'e0a7b08f-66e9-42cd-ae2f-7332749601a7');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182703045, 60000, 'f383bd87-60c7-4180-afe1-f9c2dc50c108');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182705209, 60000, 'c2985f79-df57-4de6-9773-329999a5951a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182740544, 60000, 'e422687d-efd5-4b1e-8974-99259e9f4ce6');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182763120, 60000, 'cf7e33e0-ae45-47c3-a370-9062068040e3');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182765299, 60000, '6f87df60-dfec-4caa-96c6-365cd9dc4533');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182801495, 60000, '02fd444e-f20c-4f53-8f86-e164749bd18b');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182824174, 60000, '72642ec8-0adf-4ecd-99de-53fa94bf8ff1');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182826204, 60000, 'c61f7113-40a6-46d7-84cf-24124de5a636');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182861548, 60000, '65e682f2-176e-442d-a4bc-6bbd69dc17ca');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182884225, 60000, 'da439836-8337-4665-8a14-042533cd986c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182886285, 60000, 'da6031a0-f4eb-485c-aa99-7354f5f199fa');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182921823, 60000, '5bdbee09-9b39-4a35-a73f-65220f16cd60');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182944532, 60000, 'ff6f4ea7-8cfd-4dea-81e4-14b3ff3f1194');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182946732, 60000, '66cf8e54-5606-4bdf-9e29-0e611083de63');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687182982384, 60000, '57fa9220-6233-414e-b295-0d7876683977');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183004894, 60000, '1036c606-523c-47cb-9747-8c0f00528bda');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183007072, 60000, 'b02b25f4-32c5-4d5a-8493-2d4a9ccb13c0');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183043316, 60000, '4dd73b47-97aa-4781-8fde-26123c7fcbfb');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183064904, 60000, '6d7adb44-eedd-4508-bb53-a72ac99de97d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183067087, 60000, '3d6236ad-b863-481d-a681-d7e29ea99421');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183104083, 60000, 'e56e2a4b-1ea9-4333-ae47-68229b51a0bf');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183125484, 60000, 'f9ad6d7b-664e-4b94-a4f3-91b9f5b63966');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183127696, 60000, '6a69c200-1c72-4143-a2a1-1b9a5fc42400');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183164634, 60000, 'c430300f-ed20-4d28-a65c-d00c0959822a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183186072, 60000, 'bcd32459-4fed-4360-9c49-ad94a4391e49');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183188224, 60000, '5507b772-e59f-47db-b89e-62962710c7c0');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183225674, 60000, '76fdba32-a73d-4a1d-b5fe-101b6ae9ac1a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183247005, 60000, '303161db-2ac8-4a20-8e9d-12ec20915322');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183249086, 60000, '8a062d22-cb62-4529-8622-7a4e1e81f466');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183286358, 60000, 'da4afb4a-8322-4512-89bb-f3da65964b2d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183307538, 60000, '8921f601-9fe3-452c-bc9a-ae1439ec32dd');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183309663, 60000, '74f0a76b-7536-40b3-a7c4-d188bee2a795');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183346399, 60000, 'a1643e36-8a9b-4825-a060-7715459184cd');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183367596, 60000, '0a6fb455-03d9-41c8-97a0-74957f167492');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183369669, 60000, '374e9415-0edb-4901-801a-9cce2194ba3c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183407016, 60000, 'e1a1c867-9c9b-43a0-8747-23f396bab81e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183428584, 60000, 'b59951fb-01ec-469e-9943-4a06bd7fd679');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183430670, 60000, 'dd247d2e-5e2c-4c41-8c73-8255f9732ce8');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183467094, 60000, '42a4d62f-063b-4f3f-9f2f-e716d223bec8');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183489534, 60000, '8c749058-5d74-4bc1-865a-115fb47fa407');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183491634, 60000, 'edea183f-ca9a-41fb-ab28-4a318d517112');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183528154, 60000, '3f8fa237-beae-4c2a-8d44-a5c6dca80123');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183549679, 60000, '799c8dd3-4e74-4572-bd45-0c139a31b649');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183551733, 60000, 'f70e7eee-1ff2-4b5f-92ce-2cf089d9e5bc');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183588953, 60000, '84a5e1e1-6ff1-4d3a-85b2-608271f2e41e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183610504, 60000, '91e5c30b-5cf4-44e5-818a-d4eddd4ddb8a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183612704, 60000, '23d04061-4023-4913-aae7-40e95a08359a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183649263, 60000, 'e4e31f6e-d09a-46bb-af8c-86aab4d707bc');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183670787, 60000, 'f3e20ef5-8c0f-4781-810e-be55cb2295c0');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183672903, 60000, 'ca54a548-e3a0-46a4-b1a9-6f891c35d33e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183710311, 60000, '7c3ab765-d195-4827-8c7f-cb2f73d517df');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183731638, 60000, '2941b143-6b42-4edc-88a5-b0f919a8dca3');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183733780, 60000, 'b6d6ee6d-202a-4886-8922-bb750f152b10');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183770575, 60000, '9fae1dad-9236-491e-8776-c0c7ccc59f50');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183792109, 60000, 'bb52e40a-8927-4670-b6f6-a553507a14fe');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183794273, 60000, '18d7704f-76cc-4cc9-881a-44b8a66b65dc');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183830956, 60000, '45a471de-3ee7-41da-92c5-118a9902093c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183852617, 60000, 'df281038-8d9d-480c-afd5-1206d1898431');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183854791, 60000, '8a2fc43a-26f6-4c5c-b5f1-607eb20b4e68');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183891351, 60000, 'f0154f7b-4de6-42c2-bc8f-f18a2929ccec');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183912927, 60000, '66def1d2-1867-45f6-b840-7ddb48690d3a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183915132, 60000, 'cbfb4bbf-c143-49be-9341-ab8dde4863bb');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183951526, 60000, '8be9be22-c342-4295-8775-1675c413609c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183973112, 60000, '970e6389-69ec-49d8-bbda-f06fc2ccb115');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687183975249, 60000, '3ab68bf0-d108-4369-afdc-2c213b118151');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184012378, 60000, '254e5c8b-ba28-4335-b97d-4cd148fc72af');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184033690, 60000, '39e60cf5-3f29-4059-a4ac-ead7b015cffb');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184035854, 60000, '0ae08eb5-0fc6-4d06-b656-402989ab0739');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184073404, 60000, '0aab1825-dfb3-4d8f-b200-835f053dee5d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184093946, 60000, 'c615a5f7-91de-4956-805e-10b64e80137d');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184096144, 60000, 'f9194643-4c63-482c-90d3-26aafada35b7');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184133784, 60000, '4ea532b8-ad67-456e-b87c-1f1418757c7e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184154219, 60000, 'ac43cf83-afcb-4bab-bd41-2601f49e5840');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184156384, 60000, '01d9c4e6-5b3a-428e-be21-83d71efd0c39');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184194099, 60000, '9f538886-8550-4532-9af1-d4dcb27a9d21');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184214346, 60000, '827bd6a7-88b2-4c77-b3d7-ec7275f9b593');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184216443, 60000, '8da8344c-4b14-467c-bb12-6cccea708f66');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184254117, 60000, 'b1fb567e-09eb-4e64-bc41-86f14e375e4f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184274677, 60000, 'c7c557b3-15ed-406b-9c50-03a7b66a3110');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184276812, 60000, 'de7eab16-c52e-4b7d-9f72-89ba6ac4f60c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184314324, 60000, 'f575d40e-6d99-48fa-9ea3-499e7f66d8e8');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184335694, 60000, '2cac624e-6667-4e88-8c83-6474464a0c57');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184337826, 60000, '76de1061-66dc-400c-9d2b-da038951e316');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184374477, 60000, 'a953bf98-02cb-4a0d-ba09-c5b2fca6ff39');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184395987, 60000, 'd52eb52f-2ccd-45b0-b289-bfb7d3784324');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184398075, 60000, '30de81e3-288f-4748-a2e9-745e7c7643ff');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184434803, 60000, '8df5b27c-a083-45c5-ac27-6599c5d98de1');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184456216, 60000, '0d952261-23d9-4e52-bee9-5682dae84004');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184458392, 60000, '14966161-5c5d-41c6-9d4b-a27daacb0bf8');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184495143, 60000, '7e4f337c-3a10-4057-8dae-79206dd5ba51');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184516619, 60000, 'f19a02f2-74a7-4f51-beeb-66371f92a00b');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184518746, 60000, '1c13664e-0495-4cd2-99eb-f1e7b58b9bc8');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184555671, 60000, '462a3b62-7096-4095-987d-d401d5230867');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184577533, 60000, '5cea4ccb-9a81-4cf1-936c-7e3a8bc3f53c');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184579686, 60000, '4dcfbf50-c0db-4801-b27c-f179053a7d86');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184616224, 60000, '1f2fd751-ce1a-4c87-8050-6f98c80e6fa3');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184637864, 60000, 'a356e971-77c4-49d0-9398-07d6c1b7a39a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184639984, 60000, '53c93dea-ecdb-4d59-b088-e6c398324824');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184676406, 60000, '5c7efe1b-48f6-499b-a62a-fdde37879942');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184697880, 60000, '3084b1e5-7723-491c-abbe-8519cc132523');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184700142, 60000, '26cee2a3-bc4c-4233-8eab-5b19a096bf35');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184736693, 60000, 'a3000b47-0e3a-4139-8c46-3dab4931b8af');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184757988, 60000, '7f8e39e4-d22d-42e6-833c-f3f7902aad8e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184760157, 60000, 'fb655394-63e2-4538-8e7c-cc6566867b04');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184796885, 60000, 'db22c02a-c599-4568-93da-10b89ef329da');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184818453, 60000, 'e4ae2470-76e8-46c1-9271-04130f659acd');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184820583, 60000, '8f5d3b74-25ba-4a4b-ba65-28a823048c82');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184857031, 60000, 'eecbf973-4e04-47d5-becc-843332d8326a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184879395, 60000, 'be750e5e-0d68-4a93-b06d-f8f62a8f3c5f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184881506, 60000, '815667f6-2199-447a-b735-c4b294e8900a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184917905, 60000, '26038ab7-5c1d-4fc4-86ae-a58047a4002f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184939464, 60000, 'b6291957-bccf-4959-bcfb-0344c9282a8a');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184941664, 60000, 'a688058f-37fd-46df-a78b-ca76951f1520');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687184978595, 60000, '0a76dfa1-97e8-41e6-8c19-b4c38e19aa71');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687185000164, 60000, 'ee283f1a-c440-49c7-ba57-2da12edc7a5e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687185002331, 60000, '22865691-2e38-49fc-a603-c508aae540c5');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687185038763, 60000, 'bb377646-af8d-4964-bb4a-66e841825fe5');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687185060453, 60000, '97f8e4f0-4c6e-44bc-8724-907d7d5bc985');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687185062687, 60000, 'a3201ec9-9913-41e2-8a3a-1fa934e3e78e');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687185099324, 60000, 'c56e6cce-54b0-4ed9-ab4c-318dce3e9193');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687185120937, 60000, '1e512206-84d7-4a19-8d5c-532e0b95e52f');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687185123056, 60000, '273927e8-a242-4027-a5e6-00b0abde3526');
INSERT INTO public.edc_lease (leased_by, leased_at, lease_duration, lease_id) VALUES ('example-connector', 1687185159511, 60000, '7a2d4bd5-1493-40c0-8624-abbf096cf9fe');


--
-- Data for Name: edc_contract_negotiation; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_contract_negotiation (id, correlation_id, counterparty_id, counterparty_address, protocol, type, state, state_count, state_timestamp, error_detail, agreement_id, contract_offers, trace_context, lease_id, created_at, updated_at) VALUES ('a2edd577-845a-4ccd-967f-21858aaaf245', '6ae57214-b6ea-42b0-adaa-ad64ce6c883c', 'urn:connector:example-connector', 'http://localhost:22003/api/v1/ids/data', 'ids-multipart', 1, 1200, 1, 1687179323478, NULL, 'test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', '[{"id":"test-contract-definition:bda8b77d-07f0-4fcd-a941-4f068b71f574","policy":{"permissions":[{"edctype":"dataspaceconnector:permission","uid":null,"target":"urn:artifact:ms8-asset:1.0","action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2022-05-31T22:00:00.000Z"},"operator":"GEQ"},{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2030-06-30T22:00:00.000Z"},"operator":"LT"}],"duties":[]}],"prohibitions":[],"obligations":[],"extensibleProperties":{},"inheritsFrom":null,"assigner":null,"assignee":null,"target":"urn:artifact:ms8-asset:1.0","@type":{"@policytype":"set"}},"asset":{"id":"urn:artifact:ms8-asset:1.0","createdAt":1687179246852,"properties":{"asset:prop:curatorOrganizationName":"Example GmbH","http://w3id.org/mds#transportMode":"Road","asset:prop:contenttype":"application/json","asset:prop:version":"1.0","http://w3id.org/mds#geoReferenceMethod":"Geo reference method","asset:prop:id":"urn:artifact:ms8-asset:1.0","http://w3id.org/mds#dataModel":"Data Model","http://w3id.org/mds#dataSubcategory":"Accidents","asset:prop:language":"https://w3id.org/idsa/code/EN","asset:prop:keywords":"keyword1, keyword2","asset:prop:name":"test","asset:prop:description":"My Asset Description","http://w3id.org/mds#dataCategory":"Traffic Information","asset:prop:originator":"http://localhost:22003/api/v1/ids/data"}},"provider":"urn:connector:provider","consumer":"urn:connector:consumer","offerStart":null,"offerEnd":null,"contractStart":"2023-06-19T12:55:21.833Z","contractEnd":"2024-06-18T12:55:21.833Z"}]', '{}', NULL, 1687179322690, 1687179323478);


--
-- Data for Name: edc_data_plane_instance; Type: TABLE DATA; Schema: public; Owner: edc
--



--
-- Data for Name: edc_transfer_process; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_transfer_process (transferprocess_id, type, state, state_count, state_time_stamp, created_at, trace_context, error_detail, resource_manifest, provisioned_resource_set, content_data_address, deprovisioned_resources, lease_id, updated_at, transferprocess_properties) VALUES ('398ece7e-3c60-4766-a392-4e950f0bbf5a', 'PROVIDER', 600, 1, 1687179374487, 1687179373706, '{}', NULL, '{"definitions":[]}', NULL, '{"properties":{"baseUrl":"https://google.de","method":"GET","type":"HttpData"}}', '[]', '1e512206-84d7-4a19-8d5c-532e0b95e52f', 1687179374487, '{}');
INSERT INTO public.edc_transfer_process (transferprocess_id, type, state, state_count, state_time_stamp, created_at, trace_context, error_detail, resource_manifest, provisioned_resource_set, content_data_address, deprovisioned_resources, lease_id, updated_at, transferprocess_properties) VALUES ('7d85d66a-b755-4a10-9a15-c94719913d42', 'PROVIDER', 600, 1, 1687179377740, 1687179377033, '{}', NULL, '{"definitions":[]}', NULL, '{"properties":{"baseUrl":"https://google.de","method":"GET","type":"HttpData"}}', '[]', '273927e8-a242-4027-a5e6-00b0abde3526', 1687179377740, '{}');
INSERT INTO public.edc_transfer_process (transferprocess_id, type, state, state_count, state_time_stamp, created_at, trace_context, error_detail, resource_manifest, provisioned_resource_set, content_data_address, deprovisioned_resources, lease_id, updated_at, transferprocess_properties) VALUES ('679b517e-dbaa-4e07-a1c6-9b8ec5d4fe1d', 'PROVIDER', 600, 1, 1687179352109, 1687179351221, '{}', NULL, '{"definitions":[]}', NULL, '{"properties":{"baseUrl":"https://google.de","method":"GET","type":"HttpData"}}', '[]', '7a2d4bd5-1493-40c0-8624-abbf096cf9fe', 1687179352109, '{}');


--
-- Data for Name: edc_data_request; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_data_request (datarequest_id, process_id, connector_address, protocol, connector_id, asset_id, contract_id, data_destination, managed_resources, properties, transfer_type, transfer_process_id) VALUES ('e213fc10-f61a-4aa0-80f8-7c581651f543', '679b517e-dbaa-4e07-a1c6-9b8ec5d4fe1d', 'http://localhost:23003/api/v1/ids/data', 'ids-multipart', 'urn:connector:example-connector', 'urn:artifact:ms8-asset:1.0', 'test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', '{"properties":{"baseUrl":"https://webhook.site/e542f69e-ff0a-4771-af18-0900a399137a","method":"POST","type":"HttpData"}}', true, '{}', '{"contentType":"application/octet-stream","isFinite":true}', '679b517e-dbaa-4e07-a1c6-9b8ec5d4fe1d');
INSERT INTO public.edc_data_request (datarequest_id, process_id, connector_address, protocol, connector_id, asset_id, contract_id, data_destination, managed_resources, properties, transfer_type, transfer_process_id) VALUES ('34b587d1-9a1a-4bc2-a499-24a0e6975b97', '398ece7e-3c60-4766-a392-4e950f0bbf5a', 'http://localhost:23003/api/v1/ids/data', 'ids-multipart', 'urn:connector:example-connector', 'urn:artifact:ms8-asset:1.0', 'test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', '{"properties":{"baseUrl":"https://webhook.site/e542f69e-ff0a-4771-af18-0900a399137a","method":"POST","type":"HttpData"}}', true, '{}', '{"contentType":"application/octet-stream","isFinite":true}', '398ece7e-3c60-4766-a392-4e950f0bbf5a');
INSERT INTO public.edc_data_request (datarequest_id, process_id, connector_address, protocol, connector_id, asset_id, contract_id, data_destination, managed_resources, properties, transfer_type, transfer_process_id) VALUES ('5e4cb5c8-fc8e-4231-ba86-bc5dbf7b5015', '7d85d66a-b755-4a10-9a15-c94719913d42', 'http://localhost:23003/api/v1/ids/data', 'ids-multipart', 'urn:connector:example-connector', 'urn:artifact:ms8-asset:1.0', 'test-contract-definition:2160b70f-d65e-4861-af88-f29b16f3fa7b', '{"properties":{"baseUrl":"https://webhook.site/e542f69e-ff0a-4771-af18-0900a399137a","method":"POST","type":"HttpData"}}', true, '{}', '{"contentType":"application/octet-stream","isFinite":true}', '7d85d66a-b755-4a10-9a15-c94719913d42');


--
-- Data for Name: edc_policydefinitions; Type: TABLE DATA; Schema: public; Owner: edc
--

INSERT INTO public.edc_policydefinitions (policy_id, permissions, prohibitions, duties, extensible_properties, inherits_from, assigner, assignee, target, policy_type, created_at) VALUES ('always-true', '[{"edctype":"dataspaceconnector:permission","uid":null,"target":null,"action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"ALWAYS_TRUE"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"true"},"operator":"EQ"}],"duties":[]}]', '[]', '[]', '{}', NULL, NULL, NULL, NULL, '{"@policytype":"set"}', 1687179132836);
INSERT INTO public.edc_policydefinitions (policy_id, permissions, prohibitions, duties, extensible_properties, inherits_from, assigner, assignee, target, policy_type, created_at) VALUES ('test-policy', '[{"edctype":"dataspaceconnector:permission","uid":null,"target":null,"action":{"type":"USE","includedIn":null,"constraint":null},"assignee":null,"assigner":null,"constraints":[{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2022-05-31T22:00:00.000Z"},"operator":"GEQ"},{"edctype":"AtomicConstraint","leftExpression":{"edctype":"dataspaceconnector:literalexpression","value":"POLICY_EVALUATION_TIME"},"rightExpression":{"edctype":"dataspaceconnector:literalexpression","value":"2030-06-30T22:00:00.000Z"},"operator":"LT"}],"duties":[]}]', '[]', '[]', '{}', NULL, NULL, NULL, NULL, '{"@policytype":"set"}', 1687179288561);


--
-- PostgreSQL database dump complete
--

