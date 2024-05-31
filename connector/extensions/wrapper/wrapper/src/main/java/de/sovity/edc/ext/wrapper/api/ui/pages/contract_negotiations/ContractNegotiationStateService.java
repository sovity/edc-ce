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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations;

import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationSimplifiedState;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationState;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class ContractNegotiationStateService {

    /**
     * Interpret {@link org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation#getState()} for use in our UI.
     *
     * @param code {@link org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation#getState()}, see {@link org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates#code()}
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
