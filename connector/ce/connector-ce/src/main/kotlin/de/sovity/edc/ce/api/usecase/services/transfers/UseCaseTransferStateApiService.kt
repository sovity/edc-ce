/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.transfers

import de.sovity.edc.ce.api.ui.pages.transferhistory.TransferProcessStateService
import de.sovity.edc.ce.api.usecase.model.TransferProcessStateResult
import de.sovity.edc.ce.api.utils.EdcDateUtils
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.eqAny
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext

@Service
class UseCaseTransferStateApiService(
    private val transferProcessStateService: TransferProcessStateService
) {

    fun getTransferProcessStates(dsl: DSLContext, transferIds: Collection<String>): List<TransferProcessStateResult> {
        val states = queryTransferProcessStates(dsl, transferIds)
        return states.map { buildTransferProcessStateResult(it) }
    }

    private fun buildTransferProcessStateResult(it: TransferStateRs): TransferProcessStateResult {
        val simplifiedState = transferProcessStateService.buildTransferProcessState(it.state)
        return TransferProcessStateResult.builder()
            .transferId(it.transferProcessId)
            .state(simplifiedState)
            .stateChangedAt(EdcDateUtils.utcMillisToOffsetDateTime(it.stateChangedAt))
            .isEdrConsumable(transferProcessStateService.isEdrConsumable(it.state, it.transferType))
            .build()
    }

    private data class TransferStateRs(
        val transferProcessId: String,
        val state: Int,
        val stateChangedAt: Long,
        val transferType: String,
    )

    private fun queryTransferProcessStates(
        dsl: DSLContext,
        transferIds: Collection<String>
    ): List<TransferStateRs> {
        val t = Tables.EDC_TRANSFER_PROCESS
        return dsl.select(
            TransferStateRs::transferProcessId from {
                t.TRANSFERPROCESS_ID
            },
            TransferStateRs::state from {
                t.STATE
            },
            TransferStateRs::stateChangedAt from {
                t.STATE_TIME_STAMP
            },
            TransferStateRs::transferType from {
                t.TRANSFER_TYPE
            }
        )
            .from(t)
            .where(t.TRANSFERPROCESS_ID.eqAny(transferIds.toSet()))
            .fetchInto(TransferStateRs::class.java)
    }
}
