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

package de.sovity.edc.ext.brokerserver.services.api;

import de.sovity.edc.ext.brokerserver.api.model.AuthorityPortalOrganizationMetadata;
import de.sovity.edc.ext.brokerserver.db.jooq.Tables;
import de.sovity.edc.ext.brokerserver.db.jooq.tables.records.OrganizationMetadataRecord;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.List;

@RequiredArgsConstructor
public class AuthorityPortalOrganizationMetadataApiService {

    public void setOrganizationMetadata(DSLContext dsl, List<AuthorityPortalOrganizationMetadata> organizationMetadata) {
        var records = organizationMetadata.stream().map(this::buildRecord).toList();

        dsl.deleteFrom(Tables.ORGANIZATION_METADATA).execute();
        dsl.batchInsert(records).execute();
    }

    @NotNull
    private OrganizationMetadataRecord buildRecord(AuthorityPortalOrganizationMetadata it) {
        var record = new OrganizationMetadataRecord();
        record.setMdsId(it.getMdsId());
        record.setName(it.getName());

        return record;
    }
}
