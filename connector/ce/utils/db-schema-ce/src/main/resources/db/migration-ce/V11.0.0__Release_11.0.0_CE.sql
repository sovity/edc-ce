create table edc_lease (
    leased_by varchar not null,
    leased_at bigint,
    lease_duration integer not null,
    lease_id varchar not null
        constraint lease_pk
            primary key
);

comment on column edc_lease.leased_at is 'posix timestamp of lease';
comment on column edc_lease.lease_duration is 'duration of lease in milliseconds';

create table edc_asset (
    asset_id varchar not null,
    created_at bigint not null,
    properties json default '{}',
    private_properties json default '{}',
    data_address json default '{}',
    primary key (asset_id)
);

comment on column edc_asset.properties is 'Asset properties serialized as json';
comment on column edc_asset.private_properties is 'Asset private properties serialized as json';
comment on column edc_asset.data_address is 'Asset DataAddress serialized as json';

create table edc_policydefinitions (
    policy_id varchar not null,
    created_at bigint not null,
    permissions json,
    prohibitions json,
    duties json,
    extensible_properties json,
    inherits_from varchar,
    assigner varchar,
    assignee varchar,
    target varchar,
    policy_type varchar not null,
    private_properties json,
    primary key (policy_id)
);

comment on column edc_policydefinitions.permissions is 'Java list<Permission> serialized as json';
comment on column edc_policydefinitions.prohibitions is 'Java list<Prohibition> serialized as json';
comment on column edc_policydefinitions.duties is 'Java list<Duty> serialized as json';
comment on column edc_policydefinitions.extensible_properties is 'Java Map<String,
    object> serialized as json ';
comment on column edc_policydefinitions.policy_type is ' Java PolicyType serialized as json ';

create table edc_policy_monitor (
    entry_id varchar not null primary key,
    state integer not null,
    created_at bigint not null,
    updated_at bigint not null,
    state_count integer default 0 not null,
    state_time_stamp bigint,
    trace_context json,
    error_detail varchar,
    lease_id varchar
        constraint policy_monitor_lease_lease_id_fk
            references edc_lease
            on delete set null,
    properties json,
    contract_id varchar
);

-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index policy_monitor_state on edc_policy_monitor (state, state_time_stamp);

create table edc_contract_definitions (
    created_at bigint not null,
    contract_definition_id varchar not null,
    access_policy_id varchar not null,
    contract_policy_id varchar not null,
    assets_selector json not null,
    private_properties json,
    primary key (contract_definition_id)
);

create table edc_contract_agreement (
    agr_id varchar not null
        constraint contract_agreement_pk
            primary key,
    provider_agent_id varchar,
    consumer_agent_id varchar,
    signing_date bigint,
    start_date bigint,
    end_date integer,
    asset_id varchar not null,
    policy json
);

create table edc_contract_negotiation (
    id varchar not null
        constraint contract_negotiation_pk
            primary key,
    created_at bigint not null,
    updated_at bigint not null,
    correlation_id varchar,
    counterparty_id varchar not null,
    counterparty_address varchar not null,
    protocol varchar not null,
    type varchar not null,
    state integer default 0 not null,
    state_count integer default 0,
    state_timestamp bigint,
    error_detail varchar,
    agreement_id varchar
        constraint contract_negotiation_contract_agreement_id_fk
            references edc_contract_agreement,
    contract_offers json,
    callback_addresses json,
    trace_context json,
    pending boolean default false,
    protocol_messages json,
    lease_id varchar
        constraint contract_negotiation_lease_lease_id_fk
            references edc_lease
            on delete set null
);

comment on column edc_contract_negotiation.agreement_id is 'ContractAgreement serialized as JSON';
comment on column edc_contract_negotiation.contract_offers is 'List<ContractOffer> serialized as JSON';
comment on column edc_contract_negotiation.trace_context is 'Map<String,String> serialized as JSON';

create index contract_negotiation_correlationid_index
    on edc_contract_negotiation (correlation_id);


-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index contract_negotiation_state
    on edc_contract_negotiation (state, state_timestamp);

create table edc_transfer_process (
    transferprocess_id varchar not null
        constraint transfer_process_pk
            primary key,
    type varchar not null,
    state integer not null,
    state_count integer default 0 not null,
    state_time_stamp bigint,
    created_at bigint not null,
    updated_at bigint not null,
    trace_context json,
    error_detail varchar,
    resource_manifest json,
    provisioned_resource_set json,
    content_data_address json,
    deprovisioned_resources json,
    private_properties json,
    callback_addresses json,
    pending boolean default false,
    transfer_type varchar,
    protocol_messages json,
    data_plane_id varchar,
    correlation_id varchar,
    counter_party_address varchar,
    protocol varchar,
    asset_id varchar,
    contract_id varchar,
    data_destination json,
    lease_id varchar
        constraint transfer_process_lease_lease_id_fk
            references edc_lease
            on delete set null
);

comment on column edc_transfer_process.trace_context is 'Java Map serialized as JSON';
comment on column edc_transfer_process.resource_manifest is 'java ResourceManifest serialized as JSON';
comment on column edc_transfer_process.provisioned_resource_set is 'ProvisionedResourceSet serialized as JSON';
comment on column edc_transfer_process.content_data_address is 'DataAddress serialized as JSON';
comment on column edc_transfer_process.deprovisioned_resources is 'List of deprovisioned resources, serialized as JSON';

-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index transfer_process_state on edc_transfer_process (state, state_time_stamp);

create table edc_data_plane (
    process_id varchar not null primary key,
    state integer not null,
    created_at bigint not null,
    updated_at bigint not null,
    state_count integer default 0 not null,
    state_time_stamp bigint,
    trace_context json,
    error_detail varchar,
    callback_address varchar,
    lease_id varchar
        constraint data_plane_lease_lease_id_fk
            references edc_lease
            on delete set null,
    source json,
    destination json,
    properties json,
    flow_type varchar,
    transfer_type_destination varchar default 'HttpData'
);

comment on column edc_data_plane.trace_context is 'Java Map serialized as json';
comment on column edc_data_plane.source is 'DataAddress serialized as json';
comment on column edc_data_plane.destination is 'DataAddress serialized as json';
comment on column edc_data_plane.properties is 'Java Map serialized as json';

-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index data_plane_state on edc_data_plane (state, state_time_stamp);

create table edc_data_plane_instance (
    id varchar not null primary key,
    data json,
    lease_id varchar
        constraint data_plane_instance_lease_id_fk
            references edc_lease
            on delete set null
);

create table edc_accesstokendata (
    id varchar not null primary key,
    claim_token json not null,
    data_address json not null,
    additional_properties json default '{}'
);

comment on column edc_accesstokendata.claim_token is 'ClaimToken serialized as JSON map';
comment on column edc_accesstokendata.data_address is 'DataAddress serialized as JSON map';
comment on column edc_accesstokendata.additional_properties is 'Optional Additional properties serialized as JSON map';

create table edc_business_partner_group (
    bpn varchar not null
        constraint edc_business_partner_group_pk
            primary key,
    groups json default '[] '::json not null
);

create table edc_edr_entry (
    transfer_process_id varchar not null primary key,
    agreement_id varchar not null,
    asset_id varchar not null,
    provider_id varchar not null,
    contract_negotiation_id varchar,
    created_at bigint not null
);

-- sovity EDC CE

create type contract_terminated_by as enum ('SELF', 'COUNTERPARTY');

create table sovity_contract_termination (
    contract_agreement_id varchar primary key,
    reason text not null,
    detail text not null,
    terminated_at timestamp with time zone not null,
    terminated_by contract_terminated_by not null,
    constraint agreement_fk foreign key (contract_agreement_id)
        references edc_contract_agreement (agr_id)
);
