-- User
alter type user_registration_status add value 'INVITED' before 'CREATED';
alter type user_registration_status add value 'DEACTIVATED' after 'REJECTED';
alter type user_registration_status rename value 'APPROVED' to 'ACTIVE';

alter table "user" add column created_at timestamp with time zone not null default now();

-- Organization
alter type organization_registration_status add value 'INVITED' before 'PENDING';
alter type organization_registration_status rename value 'APPROVED' to 'ACTIVE';

alter table "organization" add column created_at timestamp with time zone not null default now();
