/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_negotiations;

import de.sovity.edc.ce.api.ui.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.ce.api.ui.model.ContractNegotiationState;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiationStates;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
@Service
public class ContractNegotiationStateService {

    /**
     * Interpret {@link org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation#getState()} for use in our UI.
     *
     * @param code {@link org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation#getState()}, see {@link org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiationStates#code()}
     * @return if running
     */
    @NotNull
    public ContractNegotiationState buildContractNegotiationState(int code) {
        var contractNegotiationState = new ContractNegotiationState();
        contractNegotiationState.setCode(code);
        contractNegotiationState.setName(getName(code));
        contractNegotiationState.setSimplifiedState(getSimplifiedState(code));
        return contractNegotiationState;
    }

    /**
     * Which Transfer Process do we want to show as 'running' in our UI?
     *
     * @param code {@link ContractNegotiation#getState()}, see {@code ContractNegotiationState#code}
     * @return if running
     */
    public boolean isRunning(int code) {
        // After this there are still states about de-provisioning of resources,
        // but we don't really care much about them
        return !isError(code) && code < ContractNegotiationStates.FINALIZED.code();
    }

    /**
     * Which Transfer Process do we want to show as 'error' in our UI?
     *
     * @param code {@link ContractNegotiation#getState()}, see {@link ContractNegotiationStates#code()}
     * @return if running
     */
    public boolean isError(int code) {
        return ContractNegotiationStates.TERMINATING.code() == code || ContractNegotiationStates.TERMINATED.code() == code;
    }

    @NotNull
    private String getName(int code) {
        ContractNegotiationStates state = ContractNegotiationStates.from(code);
        if (state != null) {
            return state.name();
        }

        return "CUSTOM";
    }

    @NotNull
    private ContractNegotiationSimplifiedState getSimplifiedState(int code) {
        if (isError(code)) {
            return ContractNegotiationSimplifiedState.TERMINATED;
        }
        if (isRunning(code)) {
            return ContractNegotiationSimplifiedState.IN_PROGRESS;
        }
        return ContractNegotiationSimplifiedState.AGREED;
    }
}
