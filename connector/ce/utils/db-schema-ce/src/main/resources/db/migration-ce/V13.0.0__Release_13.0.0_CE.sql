--
--  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       Contributors to the Eclipse Foundation - initial API and implementation
--       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
--

-- tractus 0.7 -> 0.8 migrations

create table edc_federated_catalog
(
  id      varchar primary key not null,
  catalog JSON,
  marked  boolean default false
);

-- add columns
alter table edc_policydefinitions
  add column profiles JSON;


alter table edc_data_plane
  add column runtime_id varchar;
