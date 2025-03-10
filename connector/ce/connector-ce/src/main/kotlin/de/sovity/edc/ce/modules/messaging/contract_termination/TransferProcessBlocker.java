/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination;

import de.sovity.edc.ce.modules.db.DslContextFactory;
import de.sovity.edc.ce.modules.messaging.contract_termination.query.ContractAgreementIsTerminatedQuery;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.transfer.spi.observe.TransferProcessListener;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;

@RequiredArgsConstructor
public class TransferProcessBlocker implements TransferProcessListener {

    private final DslContextFactory dslContextFactory;
    private final ContractAgreementIsTerminatedQuery contractAgreementIsTerminated;

    private void stopIt(TransferProcess process) {
        val terminated = dslContextFactory.transactionResult(dsl ->
            contractAgreementIsTerminated.isTerminated(dsl, process.getContractId()));

        if (terminated) {
            val message = "Interrupted: the contract agreement %s is terminated.".formatted(process.getContractId());
            throw new IllegalStateException(message);
        }
    }

    @Override
    public void preCreated(TransferProcess process) {
        stopIt(process);
    }
}
