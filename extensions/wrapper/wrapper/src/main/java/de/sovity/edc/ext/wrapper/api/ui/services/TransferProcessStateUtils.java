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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransferProcessStateUtils {

    /**
     * Which ContractAgreement Transfer Process do we want to show as 'running' in our UI?
     *
     * @param state state
     * @return if running
     */
    public static boolean isRunning(TransferProcessStates state) {
        // After this there are still states about de-provisioning of resources
        // They should not matter as much to the use, though, who cares about the data
        return state.code() > 0 && state.code() < TransferProcessStates.COMPLETED.code();
    }
}
