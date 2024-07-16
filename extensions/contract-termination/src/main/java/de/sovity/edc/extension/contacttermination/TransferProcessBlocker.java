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

import de.sovity.edc.extension.contacttermination.query.ContractAgreementIsTerminatedQuery;
import de.sovity.edc.extension.db.directaccess.DslContextFactory;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.transfer.spi.observe.TransferProcessListener;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;

@RequiredArgsConstructor
public class TransferProcessBlocker implements TransferProcessListener {

    private final DslContextFactory dslContextFactory;
    private final ContractAgreementIsTerminatedQuery contractAgreementIsTerminated;

    @Override
    public void preRequesting(TransferProcess process) {
        val terminated = contractAgreementIsTerminated.isTerminated(dslContextFactory.newDslContext(), process.getContractId());

        if (terminated) {
            val message = "Interrupted: the contract agreement %s is terminated.".formatted(process.getContractId());
            throw new IllegalStateException(message);
        }
    }
}
