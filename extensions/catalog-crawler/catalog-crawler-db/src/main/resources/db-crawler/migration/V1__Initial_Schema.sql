-- User
create type user_registration_status as enum ('FIRST_USER', 'CREATED', 'PENDING', 'APPROVED', 'REJECTED');

create table "user"
(
    id                  text                     not null primary key,
    organization_mds_id text,
    registration_status user_registration_status not null
);

-- Organization
create type organization_registration_status as enum ('PENDING', 'APPROVED', 'REJECTED');

create table "organization"
(
    mds_id              text                             not null primary key,
    name                text                             not null,
    address             text                             not null,
    duns                text                             not null,
    url                 text                             not null,
    security_email      text                             not null,
    created_by          text                             not null,
    registration_status organization_registration_status not null,
    constraint fk_organization_created_by foreign key (created_by) references "user" (id)
);

alter table "user"
    add constraint fk_user_organization_id
        foreign key (organization_mds_id) references "organization" (mds_id);

-- Connector
create type connector_type as enum ('OWN', 'PROVIDED', 'CAAS');

create table "connector"
(
    connector_id    text                     not null primary key,
    mds_id          text                     not null,
    provider_mds_id text                     not null,
    type            connector_type           not null,
    environment     text                     not null,
    client_id       text                     not null,
    name            text                     not null,
    location        text                     not null,
    url             text                     not null,
    created_by      text                     not null,
    created_at      timestamp with time zone not null,
    constraint fk_connector_organization_id foreign key (mds_id) references "organization" (mds_id),
    constraint fk_connector_provider_id foreign key (provider_mds_id) references "organization" (mds_id),
    constraint fk_connector_created_by foreign key (created_by) references "user" (id)
);
