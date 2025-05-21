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
package de.sovity.edc.ce.api.ui.pages.transferhistory

import de.sovity.edc.ce.api.ui.model.TransferProcessSimplifiedState
import de.sovity.edc.ce.api.ui.model.TransferProcessState
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcessStates

@Service
class TransferProcessStateService {
    /**
     * Interpret [TransferProcess.getState] for use in our UI.
     *
     * @param code [TransferProcess.getState], see [TransferProcessStates.code]
     * @return if running
     */
    fun buildTransferProcessState(code: Int): TransferProcessState {
        val transferProcessState = TransferProcessState()
        transferProcessState.code = code
        transferProcessState.name = getName(code)
        transferProcessState.simplifiedState = getSimplifiedState(code)
        return transferProcessState
    }

    /**
     * Check if the transfer process is consumable via the EDR flow
     */
    fun isEdrConsumable(code: Int, transferType: String): Boolean =
        code == TransferProcessStates.STARTED.code() && transferType.startsWith("HttpData-PULL")

    /**
     * Which Transfer Process do we want to show as 'running' in our UI?
     *
     * @param code [TransferProcess.getState], see [TransferProcessStates.code]
     * @return if running
     */
    // After this there are still states about de-provisioning of resources,
    // but we don't really care much about them
    private fun isRunning(code: Int): Boolean =
        !isError(code) && code < TransferProcessStates.COMPLETED.code()

    /**
     * Which Transfer Process do we want to show as 'error' in our UI?
     *
     * @param code [TransferProcess.getState], see [TransferProcessStates.code]
     * @return if running
     */
    private fun isError(code: Int): Boolean =
        TransferProcessStates.TERMINATING.code() == code || TransferProcessStates.TERMINATED.code() == code

    private fun getName(code: Int): String {
        val state = TransferProcessStates.from(code)
        if (state != null) {
            return state.name
        }

        return "CUSTOM"
    }

    fun getSimplifiedState(code: Int): TransferProcessSimplifiedState {
        if (isError(code)) {
            return TransferProcessSimplifiedState.ERROR
        }
        if (isRunning(code)) {
            return TransferProcessSimplifiedState.RUNNING
        }
        return TransferProcessSimplifiedState.OK
    }
}
