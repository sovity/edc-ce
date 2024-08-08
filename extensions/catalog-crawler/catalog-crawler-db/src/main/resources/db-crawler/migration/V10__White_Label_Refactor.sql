alter table organization
    rename column mds_id to id;

alter table "user"
    rename column organization_mds_id to organization_id;

alter table connector
    rename column mds_id to organization_id;

alter table connector
    rename column provider_mds_id to provider_organization_id;

alter table component
    rename column mds_id to organization_id;
