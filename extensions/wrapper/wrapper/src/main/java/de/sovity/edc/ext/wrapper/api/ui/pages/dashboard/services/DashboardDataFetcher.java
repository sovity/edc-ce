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
import de.sovity.edc.ext.wrapper.utils.QueryUtils;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.contract.spi.negotiation.store.ContractNegotiationStore;
import org.eclipse.edc.connector.controlplane.services.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.connector.controlplane.services.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.query.QuerySpec;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.List;

@RequiredArgsConstructor
public class DashboardDataFetcher {
    private final ContractNegotiationStore contractNegotiationStore;
    private final TransferProcessService transferProcessService;
    private final PolicyDefinitionService policyDefinitionService;
    private final ContractDefinitionService contractDefinitionService;

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
        return transferProcessService.query(
            // TODO: WTF is this shit? .max() works but a limited query doesn't!?
            QuerySpec.Builder.newInstance().offset(0).limit(1000).build()
        ).orElseThrow(ServiceException::new).toList();
    }
}
