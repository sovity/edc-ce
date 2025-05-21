/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.negotiation

import de.sovity.edc.ce.api.ui.model.ContractAgreementTerminationInfo
import de.sovity.edc.ce.api.ui.model.ContractTerminatedBy
import de.sovity.edc.ce.api.ui.pages.contract_negotiations.ContractNegotiationStateService
import de.sovity.edc.ce.api.usecase.model.ContractNegotiationStateResult
import de.sovity.edc.ce.api.utils.EdcDateUtils
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.eqAny
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext
import java.time.OffsetDateTime
import de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy as ContractTerminatedByJooq

@Service
class UseCaseNegotiationStateApiService(
    private val contractNegotiationStateService: ContractNegotiationStateService
) {

    fun getContractNegotiationStates(
        dsl: DSLContext,
        transferIds: Collection<String>
    ): List<ContractNegotiationStateResult> {
        val states = queryContractNegotiationStates(dsl, transferIds)
        return states.map { buildContractNegotiationStateResult(it) }
    }

    private fun buildContractNegotiationStateResult(negotiation: NegotiationStateRs): ContractNegotiationStateResult {
        val simplifiedState = contractNegotiationStateService.buildContractNegotiationState(negotiation.state)

        val terminationInfo = negotiation.terminatedBy?.let {
            ContractAgreementTerminationInfo.builder()
                .reason(negotiation.terminationReason)
                .detail(negotiation.terminationDetail)
                .terminatedAt(negotiation.terminatedAt)
                .terminatedBy(
                    when (negotiation.terminatedBy) {
                        ContractTerminatedByJooq.SELF -> ContractTerminatedBy.SELF
                        ContractTerminatedByJooq.COUNTERPARTY -> ContractTerminatedBy.COUNTERPARTY
                    }
                )
                .build()
        }

        return ContractNegotiationStateResult.builder()
            .contractNegotiationId(negotiation.contractNegotiationId)
            .contractAgreementId(negotiation.contractAgreementId)
            .state(simplifiedState)
            .stateChangedAt(EdcDateUtils.utcMillisToOffsetDateTime(negotiation.stateChangedAt))
            .contractAgreementTerminationInfo(terminationInfo)
            .build()
    }

    private data class NegotiationStateRs(
        val contractNegotiationId: String,
        val contractAgreementId: String?,
        val state: Int,
        val stateChangedAt: Long,
        val terminatedBy: ContractTerminatedByJooq?,
        val terminatedAt: OffsetDateTime?,
        val terminationReason: String?,
        val terminationDetail: String?,
    )

    private fun queryContractNegotiationStates(
        dsl: DSLContext,
        transferIds: Collection<String>
    ): List<NegotiationStateRs> {
        val n = Tables.EDC_CONTRACT_NEGOTIATION
        val t = Tables.SOVITY_CONTRACT_TERMINATION
        return dsl.select(
            NegotiationStateRs::contractNegotiationId from {
                n.ID
            },
            NegotiationStateRs::contractAgreementId from {
                n.AGREEMENT_ID
            },
            NegotiationStateRs::state from {
                n.STATE
            },
            NegotiationStateRs::stateChangedAt from {
                n.STATE_TIMESTAMP
            },
            NegotiationStateRs::terminatedBy from {
                t.TERMINATED_BY
            },
            NegotiationStateRs::terminatedAt from {
                t.TERMINATED_AT
            },
            NegotiationStateRs::terminationReason from {
                t.REASON
            },
            NegotiationStateRs::terminationDetail from {
                t.DETAIL
            }
        )
            .from(n)
            .leftJoin(t).on(t.CONTRACT_AGREEMENT_ID.eq(n.AGREEMENT_ID))
            .where(n.ID.eqAny(transferIds.toSet()))
            .fetchInto(NegotiationStateRs::class.java)
    }
}
