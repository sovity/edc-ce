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

package de.sovity.edc.ext.wrapper.api.ui.services;

import de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessSimplifiedState;
import de.sovity.edc.ext.wrapper.api.ui.model.TransferProcessState;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class TransferProcessStateService {

    /**
     * Interpret {@link TransferProcess#getState()} for use in our UI.
     *
     * @param code {@link TransferProcess#getState()}, see {@link TransferProcessStates#code()}
     * @return if running
     */
    @NotNull
    public TransferProcessState buildTransferProcessState(int code) {
        var transferProcessState = new TransferProcessState();
        transferProcessState.setCode(code);
        transferProcessState.setName(getName(code));
        transferProcessState.setSimplifiedState(getSimplifiedState(code));
        return transferProcessState;
    }

    /**
     * Which Transfer Process do we want to show as 'running' in our UI?
     *
     * @param code {@link TransferProcess#getState()}, see {@link TransferProcessStates#code()}
     * @return if running
     */
    public boolean isRunning(int code) {
        // After this there are still states about de-provisioning of resources,
        // but we don't really care much about them
        return !isError(code) && code < TransferProcessStates.COMPLETED.code();
    }

    /**
     * Which Transfer Process do we want to show as 'error' in our UI?
     *
     * @param code {@link TransferProcess#getState()}, see {@link TransferProcessStates#code()}
     * @return if running
     */
    public boolean isError(int code) {
        return code < 0;
    }

    @NotNull
    private String getName(int code) {
        TransferProcessStates state = TransferProcessStates.from(code);
        if (state != null) {
            return state.name();
        }

        return "CUSTOM";
    }

    @NotNull
    private TransferProcessSimplifiedState getSimplifiedState(int code) {
        if (isError(code)) {
            return TransferProcessSimplifiedState.ERROR;
        }
        if (isRunning(code)) {
            return TransferProcessSimplifiedState.RUNNING;
        }
        return TransferProcessSimplifiedState.OK;
    }
}
