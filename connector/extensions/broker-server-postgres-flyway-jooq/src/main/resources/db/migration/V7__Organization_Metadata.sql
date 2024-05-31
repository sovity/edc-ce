-- Create table for organization metadata
create table organization_metadata
(
    mds_id              text                             not null primary key,
    name                text                             not null
);

-- Add MDS-ID column to organization table
alter table connector add column mds_id text;
update connector set mds_id = split_part(participant_id, '.', 1)
where participant_id ~ '^MDSL[A-Za-z0-9]+\.C[A-Za-z0-9]+$';
