/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination;

public interface ContractTerminationObserver {

    /**
     * Indicates that a contract termination was started by this EDC.
     */
    default void contractTerminationStartedFromThisInstance(ContractTerminationEvent contractTerminationEvent) {
    }

    /**
     * Indicates that the first step to terminate a contract, terminating a contract on this EDC instance itself, was successful.
     * The contract is now marked as terminated on this EDC's side.
     */
    default void contractTerminationCompletedOnThisInstance(ContractTerminationEvent contractTerminationEvent) {
    }

    /**
     * Indicates that a contract termination on the counterparty EDC was started.
     */
    default void contractTerminationOnCounterpartyStarted(ContractTerminationEvent contractTerminationEvent) {
    }

    /**
     * Indicates that a contract termination was started by a counterparty EDC terminated successfully
     */
    default void contractTerminatedByCounterpartyStarted(ContractTerminationEvent contractTerminationEvent) {
    }

    /**
     * Indicates that a contract termination initiated by a counterparty EDC terminated successfully
     * The contract is now marked as terminated on this EDC.
     */
    default void contractTerminatedByCounterparty(ContractTerminationEvent contractTerminationEvent) {
    }
}
