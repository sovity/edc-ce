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
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class TransferProcessStateServiceTest {
    TransferProcessStateService transferProcessStateService;

    @BeforeEach
    void setup() {
        transferProcessStateService = new TransferProcessStateService();
    }

    @Test
    void testError() {
        int code = TransferProcessStates.ERROR.code();
        var result = transferProcessStateService.buildTransferProcessState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("ERROR");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.ERROR);
    }

    @Test
    void testRunning() {
        int code = TransferProcessStates.INITIAL.code();
        var result = transferProcessStateService.buildTransferProcessState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("INITIAL");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.RUNNING);
    }

    @Test
    void testOk() {
        int code = TransferProcessStates.COMPLETED.code();
        var result = transferProcessStateService.buildTransferProcessState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("COMPLETED");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
    }

    @Test
    void testDeprovisioning() {
        // Edge case, de-provisioning is not considered "RUNNING" anymore.
        int code = TransferProcessStates.DEPROVISIONING.code();
        var result = transferProcessStateService.buildTransferProcessState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("DEPROVISIONING");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
    }

    @Test
    void testCustomError() {
        int code = -100;
        var result = transferProcessStateService.buildTransferProcessState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("CUSTOM");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.ERROR);
    }

    @Test
    void testCustomRunning() {
        int code = 299;
        var result = transferProcessStateService.buildTransferProcessState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("CUSTOM");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.RUNNING);
    }

    @Test
    void testCustomOk() {
        int code = 2000;
        var result = transferProcessStateService.buildTransferProcessState(code);
        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getName()).isEqualTo("CUSTOM");
        assertThat(result.getSimplifiedState()).isEqualTo(TransferProcessSimplifiedState.OK);
    }
}
