-- Test Data to be added after V2 so we can test subsequent migrations

insert into connector (endpoint, connector_id, created_at, last_refresh_attempt_at, last_successful_refresh_at,
                       online_status)
values ('https://my-connector.com/ids/data', 'test-connector-1', '2019-01-01 00:00:00',
        '2019-01-01 00:00:00', '2019-01-01 00:00:00', 'ONLINE');
insert into data_offer (connector_endpoint, asset_id, asset_properties, created_at, updated_at)
values ('https://my-connector.com/ids/data',
        'test-asset-1',
        '{
          "asset:prop:id": "test-asset-1"
        }',
        '2019-01-01 00:00:00',
        '2019-01-01 00:00:00'),
       ('https://my-connector.com/ids/data',
        'test-asset-2',
        '{
          "asset:prop:id": "urn:artifact:db-rail-network-2023-jan",
          "asset:prop:name": "Rail Network DB 2023 January",
          "asset:prop:version": "1.1",
          "asset:prop:originator": "https://example-connector.rail-mgmt.bahn.de/api/v1/ids/data",
          "asset:prop:originatorOrganization": "Deutsche Bahn AG",
          "asset:prop:keywords": "db, bahn, rail, Rail-Designer",
          "asset:prop:contenttype": "application/json",
          "asset:prop:description": "Train Network Map released on 10.01.2023, valid until 31.02.2023. \nFile format is xyz as exported by Rail-Designer.",
          "asset:prop:language": "https://w3id.org/idsa/code/EN",
          "asset:prop:publisher": "https://my.cool-api.gg/about",
          "asset:prop:standardLicense": "https://my.cool-api.gg/license",
          "asset:prop:endpointDocumentation": "https://my.cool-api.gg/docs",
          "http://w3id.org/mds#dataCategory": "Infrastructure and Logistics",
          "http://w3id.org/mds#dataSubcategory": "General Information About Planning Of Routes",
          "http://w3id.org/mds#dataModel": "my-data-model-001",
          "http://w3id.org/mds#geoReferenceMethod": "my-geo-reference-method",
          "http://w3id.org/mds#transportMode": "Rail"
        }',
        '2019-01-01 00:00:00',
        '2019-01-01 00:00:00');

insert into data_offer_contract_offer (contract_offer_id, connector_endpoint, asset_id, policy, created_at, updated_at)
values ('test-contract-offer-1',
        'https://my-connector.com/ids/data',
        'test-asset-1',
        '"test-policy-1"',
        '2019-01-01 00:00:00',
        '2019-01-01 00:00:00'),
       ('test-contract-offer-2',
        'https://my-connector.com/ids/data',
        'test-asset-2',
        '"test-policy-2"',
        '2019-01-01 00:00:00',
        '2019-01-01 00:00:00');

insert into broker_event_log (created_at, user_message, event, event_status, connector_endpoint, asset_id, error_stack,
                              duration_in_ms)
values ('2019-01-01 00:00:00',
        'Connector was successfully updated, and changes were incorporated',
        'CONNECTOR_UPDATED',
        'OK',
        'https://my-connector.com/ids/data',
        'test-asset-1',
        null,
        100);

insert into broker_execution_time_measurement (connector_endpoint, created_at, type, error_status, duration_in_ms)
values ('https://my-connector.com/ids/data',
        '2019-01-01 00:00:00',
        'CONNECTOR_REFRESH',
        'OK',
        100);
