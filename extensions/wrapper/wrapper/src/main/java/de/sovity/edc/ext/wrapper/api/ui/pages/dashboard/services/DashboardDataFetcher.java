/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services;

import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.ext.wrapper.api.ServiceException;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.List;

@RequiredArgsConstructor
public class DashboardDataFetcher {
    private final TransferProcessService transferProcessService;

    public int getNumberOfAssets(DSLContext dsl) {
        return dsl.fetchCount(Tables.EDC_ASSET);
    }

    public int getNumberOfPolicies(DSLContext dsl) {
        return dsl.selectCount()
            .from(Tables.EDC_POLICYDEFINITIONS)
            .fetchSingleInto(Integer.class);
    }

    public int getNumberOfContractDefinitions(DSLContext dsl) {
        return dsl.selectCount()
            .from(Tables.EDC_CONTRACT_DEFINITIONS)
            .fetchSingleInto(Integer.class);
    }

    @NotNull
    public List<TransferProcess> getTransferProcesses() {
        return transferProcessService.search(
            QuerySpec.Builder.newInstance().offset(0).limit(1000).build()
        ).orElseThrow(ServiceException::new);
    }
}
