/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.model

import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.eqAny
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcessStates
import org.jooq.Field
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.select
import de.sovity.edc.ce.db.jooq.Tables.EDC_ASSET as ASSET
import de.sovity.edc.ce.db.jooq.Tables.EDC_CONTRACT_AGREEMENT as CONTRACT_AGREEMENT
import de.sovity.edc.ce.db.jooq.Tables.EDC_CONTRACT_DEFINITIONS as CONTRACT_DEFINITIONS
import de.sovity.edc.ce.db.jooq.Tables.EDC_CONTRACT_NEGOTIATION as CONTRACT_NEGOTIATION
import de.sovity.edc.ce.db.jooq.Tables.EDC_POLICYDEFINITIONS as POLICY_DEFINITIONS
import de.sovity.edc.ce.db.jooq.Tables.EDC_TRANSFER_PROCESS as TRANSFER_PROCESS

object DashboardFields {

    private val errorStatesCodes = listOf(
        TransferProcessStates.TERMINATED,
        TransferProcessStates.TERMINATING,
    ).map { it.code() }

    fun getNumberOfAssets(): Field<Int> =
        field(select(count()).from(ASSET))

    fun getNumberOfPolicies(): Field<Int> =
        field(select(count()).from(POLICY_DEFINITIONS))

    fun getNumberOfContractDefinitions(): Field<Int> =
        field(select(count()).from(CONTRACT_DEFINITIONS))

    fun countTransferProcessesWhereType(type: TransferProcess.Type): Field<Int> =
        field(
            select(count())
                .from(TRANSFER_PROCESS)
                .where(TRANSFER_PROCESS.TYPE.eq(type.name))
        )

    fun countTransferProcessesInErrorState(type: TransferProcess.Type): Field<Int> =
        field(
            select(count())
                .from(TRANSFER_PROCESS)
                .where(
                    TRANSFER_PROCESS.TYPE.eq(type.name),
                    TRANSFER_PROCESS.STATE.eqAny(errorStatesCodes)
                )
        )

    fun countTransferProcessesInRunningState(type: TransferProcess.Type): Field<Int> =
        field(
            select(count())
                .from(TRANSFER_PROCESS)
                .where(
                    TRANSFER_PROCESS.TYPE.eq(type.name),
                    TRANSFER_PROCESS.STATE.notIn(errorStatesCodes),
                    TRANSFER_PROCESS.STATE.lt(TransferProcessStates.COMPLETED.code())
                )
        )

    fun countTransferProcessesInOkState(type: TransferProcess.Type): Field<Int> =
        field(
            select(count())
                .from(TRANSFER_PROCESS)
                .where(
                    TRANSFER_PROCESS.TYPE.eq(type.name),
                    TRANSFER_PROCESS.STATE.notIn(errorStatesCodes),
                    TRANSFER_PROCESS.STATE.ge(TransferProcessStates.COMPLETED.code())
                )
        )

    fun agreementCount(type: ContractNegotiation.Type): Field<Int> =
        field(
            select(count())
                .from(CONTRACT_NEGOTIATION)
                .leftJoin(CONTRACT_AGREEMENT)
                .on(CONTRACT_NEGOTIATION.AGREEMENT_ID.eq(CONTRACT_AGREEMENT.AGR_ID))
                .where(CONTRACT_NEGOTIATION.TYPE.eq(type.toString()))
        )
}
