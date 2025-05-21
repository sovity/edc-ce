/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services

import de.sovity.edc.ce.api.ui.model.TransferProcessSimplifiedState
import de.sovity.edc.ce.api.usecase.model.KpiResult
import de.sovity.edc.ce.api.usecase.model.TransferProcessStatesDto
import de.sovity.edc.ce.api.usecase.services.model.KpiDao
import de.sovity.edc.runtime.simple_di.Service
import lombok.RequiredArgsConstructor
import org.jooq.DSLContext

@RequiredArgsConstructor
@Service
class KpiApiService(
    private val kpiDao: KpiDao
) {
    fun getKpis(dsl: DSLContext): KpiResult {
        val kpiRs = kpiDao.getKpis(dsl)

        return KpiResult(
            kpiRs.assetsCount,
            kpiRs.policiesCount,
            kpiRs.contractDefinitionsCount,
            kpiRs.contractAgreementsCount,
            TransferProcessStatesDto.builder()
                .incomingTransferProcessCounts(
                    mapOf(
                        TransferProcessSimplifiedState.RUNNING to kpiRs.incomingTransferProcessRunningCount,
                        TransferProcessSimplifiedState.OK to kpiRs.incomingTransferProcessOkCount,
                        TransferProcessSimplifiedState.ERROR to kpiRs.incomingTransferProcessErrorCount,
                    )
                )
                .outgoingTransferProcessCounts(
                    mapOf(
                        TransferProcessSimplifiedState.RUNNING to kpiRs.outgoingTransferProcessRunningCount,
                        TransferProcessSimplifiedState.OK to kpiRs.outgoingTransferProcessOkCount,
                        TransferProcessSimplifiedState.ERROR to kpiRs.outgoingTransferProcessErrorCount,
                    )
                )
                .build()
        )
    }
}
