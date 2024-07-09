-- Components
create table "component" (
    id              text primary key,
    mds_id          text                     not null,
    name            text                     not null,
    homepage_url    text,
    endpoint_url    text                     not null,
    environment     text                     not null,
    client_id       text                     not null,
    created_by      text                     not null,
    created_at      timestamp with time zone not null default now(),
    constraint fk_component_created_by foreign key (created_by) references "user" (id),
    constraint fk_component_organization_id foreign key (mds_id) references "organization" (mds_id)
);
