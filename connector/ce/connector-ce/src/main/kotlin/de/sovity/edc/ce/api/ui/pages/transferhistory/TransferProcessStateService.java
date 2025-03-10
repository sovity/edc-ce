/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer ISST - contributions to the Eclipse EDC 0.2.0 migration
 */
package de.sovity.edc.ce.api.ui.pages.transferhistory;

import de.sovity.edc.ce.api.ui.model.TransferProcessSimplifiedState;
import de.sovity.edc.ce.api.ui.model.TransferProcessState;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcessStates;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
@Service
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
        return TransferProcessStates.TERMINATING.code() == code || TransferProcessStates.TERMINATED.code() == code;
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
    public TransferProcessSimplifiedState getSimplifiedState(int code) {
        if (isError(code)) {
            return TransferProcessSimplifiedState.ERROR;
        }
        if (isRunning(code)) {
            return TransferProcessSimplifiedState.RUNNING;
        }
        return TransferProcessSimplifiedState.OK;
    }
}
