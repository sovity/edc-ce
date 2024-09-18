alter type connector_type
    add value 'CONFIGURING' after 'CAAS';

alter table connector
    drop column broker_registration_status;
