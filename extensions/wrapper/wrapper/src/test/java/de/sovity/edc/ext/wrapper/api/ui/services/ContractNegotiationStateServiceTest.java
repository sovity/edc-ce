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

package de.sovity.edc.ext.wrapper.api.ui.services;

import de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractNegotiationStateService;
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
    void testError() {
        int code = ContractNegotiationStates.TERMINATED.code();
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("TERMINATED");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.ERROR);
    }

    @Test
    void testRunning() {
        int code = ContractNegotiationStates.OFFERING.code();
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("INITIAL");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.RUNNING);
    }

    @Test
    void testOk() {
        int code = ContractNegotiationStates.FINALIZED.code();
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("COMPLETED");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
    }

    @Test
    void testDeprovisioning() {
        // Edge case, de-provisioning is not considered "RUNNING" anymore.
        int code = ContractNegotiationStates.TERMINATED.code();
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("TERMINATED");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
    }

    @Test
    void testCustomRunning() {
        int code = 299;
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("CUSTOM");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.RUNNING);
    }

    @Test
    void testCustomOk() {
        int code = 2000;
        var result = contractNegotiationStateService.buildContractNegotiationState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("CUSTOM");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
    }
}
