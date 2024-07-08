--
--  Copyright (c) 2023 sovity GmbH
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       sovity GmbH - initial API and implementation for DataplaneInstances
--
--
CREATE TABLE IF NOT EXISTS edc_data_plane_instance
(
    id   VARCHAR NOT NULL,
    data JSON    NOT NULL,
    PRIMARY KEY (id)
);
