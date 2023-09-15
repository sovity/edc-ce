/*
 *  Copyright (c) 2023 sovity GmbH
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
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiationStates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ContractNegotiationStateServiceTest {
    ContractNegotiationStateService contractNegotiationStateService;

    @BeforeEach
    void setup() {
        contractNegotiationStateService = new ContractNegotiationStateService();
    }

    @Test
    void testTerminating() {
        // Edge case, terminating is not considered "RUNNING" anymore.
        int code = ContractNegotiationStates.TERMINATING.code();
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("TERMINATING");
        assertThat(result.getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.TERMINATED);
    }

    @Test
    void testTerminated() {
        int code = ContractNegotiationStates.TERMINATED.code();
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("TERMINATED");
        assertThat(result.getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.TERMINATED);
    }

    @Test
    void testRunning() {
        int code = ContractNegotiationStates.INITIAL.code();
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("INITIAL");
        assertThat(result.getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.IN_PROGRESS);
    }

    @Test
    void testFinalized() {
        int code = ContractNegotiationStates.FINALIZED.code();
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("FINALIZED");
        assertThat(result.getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.AGREED);
    }

    @Test
    void testCustomRunning() {
        int code = 299;
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("CUSTOM");
        assertThat(result.getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.IN_PROGRESS);
    }

    @Test
    void testCustomOk() {
        int code = 2000;
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("CUSTOM");
        assertThat(result.getSimplifiedState()).isEqualTo(ContractNegotiationSimplifiedState.AGREED);
    }
}
