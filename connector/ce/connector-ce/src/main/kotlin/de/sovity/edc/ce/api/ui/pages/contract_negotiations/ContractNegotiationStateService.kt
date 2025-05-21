/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_negotiations

import de.sovity.edc.ce.api.ui.model.ContractNegotiationSimplifiedState
import de.sovity.edc.ce.api.ui.model.ContractNegotiationState
import de.sovity.edc.ce.db.jooq.tables.EdcContractNegotiation
import de.sovity.edc.runtime.simple_di.Service
import lombok.RequiredArgsConstructor
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiationStates
import org.jooq.Condition
import org.jooq.impl.DSL

@RequiredArgsConstructor
@Service
class ContractNegotiationStateService {
    /**
     * Interpret [org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation.getState] for use in our UI.
     *
     * @param code [org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation.getState], see [org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiationStates.code]
     * @return if running
     */
    fun buildContractNegotiationState(code: Int): ContractNegotiationState {
        val contractNegotiationState = ContractNegotiationState()
        contractNegotiationState.code = code
        contractNegotiationState.name = getName(code)
        contractNegotiationState.simplifiedState = getSimplifiedState(code)
        return contractNegotiationState
    }

    /**
     * Which Transfer Process do we want to show as 'running' in our UI?
     *
     * @param code [ContractNegotiation.getState], see `ContractNegotiationState#code`
     * @return if running
     */
    fun isRunning(code: Int): Boolean =
    // After this there are still states about de-provisioning of resources,
        // but we don't really care much about them
        !isError(code) && code < ContractNegotiationStates.FINALIZED.code()

    fun isRunningDb(n: EdcContractNegotiation): Condition =
        DSL.and(isErrorDb(n).not(), n.STATE.lt(ContractNegotiationStates.FINALIZED.code()))

    fun isOkDb(n: EdcContractNegotiation): Condition =
        n.STATE.eq(ContractNegotiationStates.FINALIZED.code())

    /**
     * Which Transfer Process do we want to show as 'error' in our UI?
     *
     * @param code [ContractNegotiation.getState], see [ContractNegotiationStates.code]
     * @return if running
     */
    fun isError(code: Int): Boolean =
        ContractNegotiationStates.TERMINATING.code() == code ||
            ContractNegotiationStates.TERMINATED.code() == code

    fun isErrorDb(n: EdcContractNegotiation): Condition =
        DSL.or(
            n.STATE.eq(ContractNegotiationStates.TERMINATING.code()),
            n.STATE.eq(ContractNegotiationStates.TERMINATED.code()),
        )

    private fun getName(code: Int): String {
        val state = ContractNegotiationStates.from(code)
        if (state != null) {
            return state.name
        }

        return "CUSTOM"
    }

    private fun getSimplifiedState(code: Int): ContractNegotiationSimplifiedState {
        if (isError(code)) {
            return ContractNegotiationSimplifiedState.TERMINATED
        }
        if (isRunning(code)) {
            return ContractNegotiationSimplifiedState.IN_PROGRESS
        }
        return ContractNegotiationSimplifiedState.AGREED
    }
}
