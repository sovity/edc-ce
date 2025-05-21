/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.model

import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.eqAny
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcessStates
import org.jooq.Field
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.select
import de.sovity.edc.ce.db.jooq.Tables.EDC_ASSET as ASSET
import de.sovity.edc.ce.db.jooq.Tables.EDC_CONTRACT_AGREEMENT as CONTRACT_AGREEMENT
import de.sovity.edc.ce.db.jooq.Tables.EDC_CONTRACT_DEFINITIONS as CONTRACT_DEFINITIONS
import de.sovity.edc.ce.db.jooq.Tables.EDC_POLICYDEFINITIONS as POLICY_DEFINITIONS
import de.sovity.edc.ce.db.jooq.Tables.EDC_TRANSFER_PROCESS as TRANSFER_PROCESS

object KpiFields {

    private val errorStatesCodes = listOf(
        TransferProcessStates.TERMINATED,
        TransferProcessStates.TERMINATING,
    ).map { it.code() }

    fun getAssetsCount(): Field<Int> =
        field(select(count()).from(ASSET))

    fun getPoliciesCount(): Field<Int> =
        field(select(count()).from(POLICY_DEFINITIONS))

    fun getContractDefinitionsCount(): Field<Int> =
        field(select(count()).from(CONTRACT_DEFINITIONS))

    fun getContractAgreementCount(): Field<Int> =
        field(select(count()).from(CONTRACT_AGREEMENT))

    fun getTransferProcessesCount(): Field<Int> =
        field(select(count()).from(TRANSFER_PROCESS))


    fun getTransferProcessesInErrorStateCount(type: TransferProcess.Type): Field<Int> =
        field(
            select(count())
                .from(TRANSFER_PROCESS)
                .where(
                    TRANSFER_PROCESS.TYPE.eq(type.name),
                    TRANSFER_PROCESS.STATE.eqAny(errorStatesCodes)
                )
        )

    fun getTransferProcessesInRunningStateCount(type: TransferProcess.Type): Field<Int> =
        field(
            select(count())
                .from(TRANSFER_PROCESS)
                .where(
                    TRANSFER_PROCESS.TYPE.eq(type.name),
                    TRANSFER_PROCESS.STATE.notIn(errorStatesCodes),
                    TRANSFER_PROCESS.STATE.lt(TransferProcessStates.COMPLETED.code())
                )
        )

    fun getTransferProcessesInOkStateCount(type: TransferProcess.Type): Field<Int> =
        field(
            select(count())
                .from(TRANSFER_PROCESS)
                .where(
                    TRANSFER_PROCESS.TYPE.eq(type.name),
                    TRANSFER_PROCESS.STATE.notIn(errorStatesCodes),
                    TRANSFER_PROCESS.STATE.ge(TransferProcessStates.COMPLETED.code())
                )
        )

}
