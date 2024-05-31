-- Changes to Enums are non-transactional and must be supplied in a separate migration script for flyway

-- Connector Data Offer Limit was exceeded
alter type broker_event_type add value 'CONNECTOR_DATA_OFFER_LIMIT_EXCEEDED';
-- Connector Data Offer Limit was not exceeded
alter type broker_event_type add value 'CONNECTOR_DATA_OFFER_LIMIT_OK';
-- Connector Contract Offer Limit was exceeded
alter type broker_event_type add value 'CONNECTOR_CONTRACT_OFFER_LIMIT_EXCEEDED';
-- Connector Contract Offer Limit was not exceeded
alter type broker_event_type add value 'CONNECTOR_CONTRACT_OFFER_LIMIT_OK';
