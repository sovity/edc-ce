-- Edit Enum: Remove UserRegistrationStatus.CREATED, UserRegistrationStatus.FIRST_USER
delete
from "user"
where registration_status = 'CREATED'
   or registration_status = 'FIRST_USER';

create type tmp_enum AS ENUM ('INVITED', 'ONBOARDING', 'PENDING', 'ACTIVE', 'REJECTED', 'DEACTIVATED');

alter table "user"
    alter column registration_status type tmp_enum
        using (registration_status::text::tmp_enum);

drop type user_registration_status;

alter type tmp_enum rename to user_registration_status;

-- Component & Connector online status
create type component_type as enum ('BROKER', 'DAPS', 'LOGGING_HOUSE');
create type component_online_status as enum ('UP', 'DOWN', 'PENDING', 'MAINTENANCE');
create table "component_downtimes"
(
    component   component_type           not null,
    status      component_online_status  not null,
    environment text                     not null,
    time_stamp  timestamp with time zone not null,
    primary key (component, environment, time_stamp)
);
create index component_downtimes_time_stamp_index on "component_downtimes" (time_stamp);

create type connector_uptime_status as enum ('UP', 'DOWN', 'DEAD');
create table "connector_downtimes"
(
    connector_id text                     not null,
    status       connector_uptime_status  not null,
    environment  text                     not null,
    time_stamp   timestamp with time zone not null,
    primary key (connector_id, time_stamp)
);
create index connector_downtimes_time_stamp_index on "connector_downtimes" (time_stamp);
