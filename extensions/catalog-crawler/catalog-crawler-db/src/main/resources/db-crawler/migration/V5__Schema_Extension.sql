-- User
create type user_onboarding_type as enum ('INVITATION', 'SELF_REGISTRATION');

alter table "user"
    add column onboarding_type user_onboarding_type,
    add column invited_by      text,
    add constraint fk_invited_by foreign key (invited_by) references "user" (id);

update "user" set onboarding_type = 'SELF_REGISTRATION' where onboarding_type is null;

-- Connector
create type connector_broker_registration_status as enum ('REGISTERED', 'UNREGISTERED');
create type caas_status as enum ('INIT', 'PROVISIONING', 'AWAITING_RUNNING', 'RUNNING', 'DEPROVISIONING', 'AWAITING_STOPPED', 'STOPPED', 'ERROR', 'NOT_FOUND');

alter table "connector"
    add column broker_registration_status connector_broker_registration_status not null default 'UNREGISTERED',
    add column management_url             text,
    add column endpoint_url               text,
    add column jwks_url                   text,
    add column caas_status                caas_status,
    alter column provider_mds_id drop not null,
    alter column url drop not null;

alter table "connector"
    rename column url to frontend_url;

-- Fallback in case someone tries to migrate from 0.x to 1.0
update "connector"
set management_url = frontend_url || '/api/management' where management_url is null;

update "connector"
set endpoint_url = frontend_url || '/api/dsp' where endpoint_url is null;

-- Organization id type
create type organization_legal_id_type as enum ('TAX_ID', 'COMMERCE_REGISTER_INFO');
alter table "organization"
    add column legal_id_type organization_legal_id_type,
    add column description text,
    alter column address drop not null,
    alter column url drop not null;
update organization set legal_id_type = 'TAX_ID' where organization.tax_id is not null;
update organization set legal_id_type = 'COMMERCE_REGISTER_INFO' where organization.commerce_register_number is not null and tax_id is null;

-- New registration flow
alter type user_registration_status add value 'ONBOARDING' after 'CREATED';
alter type organization_registration_status add value 'ONBOARDING' after 'INVITED';
