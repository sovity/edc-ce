/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver.dao.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@Getter
@ToString
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ConnectorRecord {
    String id;
    String idsId;
    String title;
    String description;
    String endpoint;
    OffsetDateTime lastUpdate;
    OffsetDateTime offlineSince;
    OffsetDateTime createdAt;
    OnlineStatus onlineStatus;
}
