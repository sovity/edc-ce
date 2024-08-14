/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.contacttermination;

import de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy;
import org.eclipse.edc.spi.observe.Observable;

import java.time.OffsetDateTime;

import static de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy.SELF;

public interface ContractTerminationObserver {

    /**
     * Indicates that a contract termination was started by this EDC.
     */
    default void contractTerminationStartedFromThisInstance() {
    }

    /**
     * Indicates that the first step to terminate a contract, terminating a contract on this EDC instance itself, was successful.
     * The contract is now marked as terminated on this EDC's side.
     *
     * @param terminatedAt The termination time, as saved in the database.
     */
    default void contractTerminationCompletedOnThisInstance(OffsetDateTime terminatedAt) {
    }

    /**
     * Indicates that a contract termination on the counterparty EDC was started.
     */
    default void contractTerminationOnCounterpartyStarted() {
    }

    /**
     * Indicates that a contract termination was started by a counterparty EDC terminated successfully
     */
    default void contractTerminatedByCounterpartyStarted() {
    }

    /**
     * Indicates that a contract termination initiated by a counterparty EDC terminated successfully
     * The contract is now marked as terminated on this EDC.
     */
    default void contractTerminatedByCounterparty() {
    }
}
