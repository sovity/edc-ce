-- Aggregated baseline from the Eclipse Dataspace Connector V0.7.2

---

-- extensions/common/store/sql/edr-index-sql/docs/schema.sql
create table edc_edr_entry
(
    transfer_process_id     VARCHAR not null primary key,
    agreement_id            VARCHAR not null,
    asset_id                VARCHAR not null,
    provider_id             VARCHAR not null,
    contract_negotiation_id VARCHAR,
    created_at              BIGINT  not null
);

---

-- extensions/control-plane/store/sql/contract-negotiation-store-sql/docs/schema.sql
-- Statements are designed for and tested with Postgres only!

create table edc_lease
(
    leased_by      VARCHAR not null,
    leased_at      BIGINT,
    lease_duration INTEGER not null,
    lease_id       VARCHAR not null
        constraint lease_pk
            primary key
);

comment on column edc_lease.leased_at is 'posix timestamp of lease';

comment on column edc_lease.lease_duration is 'duration of lease in milliseconds';



create table edc_contract_agreement
(
    agr_id            VARCHAR not null
        constraint contract_agreement_pk
            primary key,
    provider_agent_id VARCHAR,
    consumer_agent_id VARCHAR,
    signing_date      BIGINT,
    start_date        BIGINT,
    end_date          INTEGER,
    asset_id          VARCHAR not null,
    policy            JSON
);


create table edc_contract_negotiation
(
    id                   VARCHAR           not null
        constraint contract_negotiation_pk
            primary key,
    created_at           BIGINT            not null,
    updated_at           BIGINT            not null,
    correlation_id       VARCHAR,
    counterparty_id      VARCHAR           not null,
    counterparty_address VARCHAR           not null,
    protocol             VARCHAR           not null,
    type                 VARCHAR           not null,
    state                INTEGER default 0 not null,
    state_count          INTEGER default 0,
    state_timestamp      BIGINT,
    error_detail         VARCHAR,
    agreement_id         VARCHAR
        constraint contract_negotiation_contract_agreement_id_fk
            references edc_contract_agreement,
    contract_offers      JSON,
    callback_addresses   JSON,
    trace_context        JSON,
    pending              BOOLEAN default false,
    protocol_messages    JSON,
    lease_id             VARCHAR
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
create index contract_negotiation_state on edc_contract_negotiation (state, state_timestamp);

---

-- extensions/control-plane/store/sql/transfer-process-store-sql/docs/schema.sql
-- Statements are designed for and tested with Postgres only!

comment on column edc_lease.leased_at is 'posix timestamp of lease';

comment on column edc_lease.lease_duration is 'duration of lease in milliseconds';

create table edc_transfer_process
(
    transferprocess_id       VARCHAR           not null
        constraint transfer_process_pk
            primary key,
    type                     VARCHAR           not null,
    state                    INTEGER           not null,
    state_count              INTEGER default 0 not null,
    state_time_stamp         BIGINT,
    created_at               BIGINT            not null,
    updated_at               BIGINT            not null,
    trace_context            JSON,
    error_detail             VARCHAR,
    resource_manifest        JSON,
    provisioned_resource_set JSON,
    content_data_address     JSON,
    deprovisioned_resources  JSON,
    private_properties       JSON,
    callback_addresses       JSON,
    pending                  BOOLEAN default false,
    transfer_type            VARCHAR,
    protocol_messages        JSON,
    data_plane_id            VARCHAR,
    correlation_id           VARCHAR,
    counter_party_address    VARCHAR,
    protocol                 VARCHAR,
    asset_id                 VARCHAR,
    contract_id              VARCHAR,
    data_destination         JSON,
    lease_id                 VARCHAR
        constraint transfer_process_lease_lease_id_fk
            references edc_lease
            on delete set null
);

comment on column edc_transfer_process.trace_context is 'Java Map serialized as JSON';

comment on column edc_transfer_process.resource_manifest is 'java ResourceManifest serialized as JSON';

comment on column edc_transfer_process.provisioned_resource_set is 'ProvisionedResourceSet serialized as JSON';

comment on column edc_transfer_process.content_data_address is 'DataAddress serialized as JSON';

comment on column edc_transfer_process.deprovisioned_resources is 'List of deprovisioned resources, serialized as JSON';


create unique index transfer_process_id_uindex
    on edc_transfer_process (transferprocess_id);


-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index transfer_process_state on edc_transfer_process (state, state_time_stamp);

---

-- extensions/data-plane-selector/store/sql/data-plane-instance-store-sql/docs/schema.sq


create table edc_data_plane_instance
(
    id       VARCHAR not null primary key,
    data     JSON,
    lease_id VARCHAR
        constraint data_plane_instance_lease_id_fk
            references edc_lease
            on delete set null
);

---

-- extensions/data-plane/store/sql/accesstokendata-store-sql/docs/schema.sql
/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

-- Statements are designed for and tested with Postgres only!

create table edc_accesstokendata
(
    id                    VARCHAR not null primary key,
    claim_token           JSON    not null,
    data_address          JSON    not null,
    additional_properties JSON default '{}'
);

comment on column edc_accesstokendata.claim_token is 'ClaimToken serialized as JSON map';
comment on column edc_accesstokendata.data_address is 'DataAddress serialized as JSON map';
comment on column edc_accesstokendata.additional_properties is 'Optional Additional properties serialized as JSON map';

---

-- extensions/data-plane/store/sql/data-plane-store-sql/docs/schema.sql
-- Statements are designed for and tested with Postgres only!

comment on column edc_lease.leased_at is 'posix timestamp of lease';
comment on column edc_lease.lease_duration is 'duration of lease in milliseconds';

create table edc_data_plane
(
    process_id       VARCHAR           not null primary key,
    state            INTEGER           not null,
    created_at       BIGINT            not null,
    updated_at       BIGINT            not null,
    state_count      INTEGER default 0 not null,
    state_time_stamp BIGINT,
    trace_context    JSON,
    error_detail     VARCHAR,
    callback_address VARCHAR,
    lease_id         VARCHAR
        constraint data_plane_lease_lease_id_fk
            references edc_lease
            on delete set null,
    source           JSON,
    destination      JSON,
    properties       JSON,
    flow_type        VARCHAR
);

comment on column edc_data_plane.trace_context is 'Java Map serialized as JSON';
comment on column edc_data_plane.source is 'DataAddress serialized as JSON';
comment on column edc_data_plane.destination is 'DataAddress serialized as JSON';
comment on column edc_data_plane.properties is 'Java Map serialized as JSON';

-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index data_plane_state on edc_data_plane (state, state_time_stamp);

---

-- extensions/policy-monitor/store/sql/policy-monitor-store-sql/docs/schema.sql
-- Statements are designed for and tested with Postgres only!

comment on column edc_lease.leased_at is 'posix timestamp of lease';
comment on column edc_lease.lease_duration is 'duration of lease in milliseconds';

create table edc_policy_monitor
(
    entry_id         VARCHAR           not null primary key,
    state            INTEGER           not null,
    created_at       BIGINT            not null,
    updated_at       BIGINT            not null,
    state_count      INTEGER default 0 not null,
    state_time_stamp BIGINT,
    trace_context    JSON,
    error_detail     VARCHAR,
    lease_id         VARCHAR
        constraint policy_monitor_lease_lease_id_fk
            references edc_lease
            on delete set null,
    properties       JSON,
    contract_id      VARCHAR
);


-- This will help to identify states that need to be transitioned without a table scan when the entries grow
create index policy_monitor_state on edc_policy_monitor (state, state_time_stamp);

---

-- extensions/common/sql/sql-lease/src/test/resources/schema.sql
-- Statements are designed for and tested with Postgres only!

comment on column edc_lease.leased_at is 'posix timestamp of lease';

comment on column edc_lease.lease_duration is 'duration of lease in milliseconds';


-- test entity
create table edc_test_entity
(
    id       VARCHAR not null
        constraint test_id_pk primary key,
    lease_id VARCHAR
        constraint test_entity_lease_id_fk
            references edc_lease
            on delete set null
);

create unique index test_entity_id_uindex
    on edc_test_entity (id);

---

-- extensions/control-plane/store/sql/asset-index-sql/docs/schema.sql
--
--  Copyright (c) 2022 - 2023 Daimler TSS GmbH
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       Daimler TSS GmbH - Initial SQL Query
--       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - improvements
--

-- THIS SCHEMA HAS BEEN WRITTEN AND TESTED ONLY FOR POSTGRES

-- table: edc_asset
create table edc_asset
(
    asset_id           VARCHAR not null,
    created_at         BIGINT  not null,
    properties         JSON default '{}',
    private_properties JSON default '{}',
    data_address       JSON default '{}',
    primary key (asset_id)
);

comment on column edc_asset.properties is 'Asset properties serialized as JSON';
comment on column edc_asset.private_properties is 'Asset private properties serialized as JSON';
comment on column edc_asset.data_address is 'Asset DataAddress serialized as JSON';


---

-- extensions/control-plane/store/sql/contract-definition-store-sql/docs/schema.sql
--
--  Copyright (c) 2022 Daimler TSS GmbH
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       Daimler TSS GmbH - Initial SQL Query
--       Microsoft Corporation - refactoring
--       SAP SE - add private properties to contract definition
--

-- table: edc_contract_definitions
-- only intended for and tested with H2 and Postgres!
create table edc_contract_definitions
(
    created_at             BIGINT  not null,
    contract_definition_id VARCHAR not null,
    access_policy_id       VARCHAR not null,
    contract_policy_id     VARCHAR not null,
    assets_selector        JSON    not null,
    private_properties     JSON,
    primary key (contract_definition_id)
);

---

-- extensions/control-plane/store/sql/policy-definition-store-sql/docs/schema.sql
--
--  Copyright (c) 2022 ZF Friedrichshafen AG
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       ZF Friedrichshafen AG - Initial SQL Query
--

-- Statements are designed for and tested with Postgres only!

-- table: edc_policydefinitions
create table if not exists edc_policydefinitions
(
    policy_id             VARCHAR not null,
    created_at            BIGINT  not null,
    permissions           JSON,
    prohibitions          JSON,
    duties                JSON,
    extensible_properties JSON,
    inherits_from         VARCHAR,
    assigner              VARCHAR,
    assignee              VARCHAR,
    target                VARCHAR,
    policy_type           VARCHAR not null,
    private_properties    JSON,
    primary key (policy_id)
);

comment on column edc_policydefinitions.permissions is 'Java List<Permission> serialized as JSON';
comment on column edc_policydefinitions.prohibitions is 'Java List<Prohibition> serialized as JSON';
comment on column edc_policydefinitions.duties is 'Java List<Duty> serialized as JSON';
comment on column edc_policydefinitions.extensible_properties is 'Java Map<String, Object> serialized as JSON';
comment on column edc_policydefinitions.policy_type is 'Java PolicyType serialized as JSON';

create unique index if not exists edc_policydefinitions_id_uindex
    on edc_policydefinitions (policy_id);

---

/*
 * Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

-- sovity

create type contract_terminated_by as enum ('SELF', 'COUNTERPARTY');

create table sovity_contract_termination
(
    contract_agreement_id varchar primary key,
    reason                text                     not null,
    detail                text                     not null,
    terminated_at         timestamp with time zone not null,
    terminated_by         contract_terminated_by   not null,
    constraint agreement_fk foreign key (contract_agreement_id)
        references edc_contract_agreement (agr_id)
);

-- Drop the duplicate indexes if they exist for improved resource usage.
drop index if exists edc_policydefinitions_id_uindex;
drop index if exists transfer_process_id_uindex;

-- This will mark the existing contracts as terminated and pending termination
-- The goal is to not use the old contracts anymore and inform the counterparty that it's been cancelled

alter table edc_contract_agreement
    add column sovity_marked_for_termination boolean default false;

-- Mark all the existing contracts to be terminated
update edc_contract_agreement
set sovity_marked_for_termination = true;

