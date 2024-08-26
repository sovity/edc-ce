/*
 *  Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.mdslogginhousebinder;

import de.sovity.edc.extension.contacttermination.ContractAgreementTerminationService;
import de.sovity.edc.extension.contacttermination.ContractTerminationEvent;
import de.sovity.edc.extension.contacttermination.ContractTerminationObserver;
import lombok.val;
import org.eclipse.edc.connector.transfer.spi.observe.TransferProcessObservable;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.event.EventRouter;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.uuid.UuidGenerator;

public class MdsLoggingHouseBinder implements ServiceExtension {

    @Inject
    private EventRouter eventRouter;

    @Inject
    private Monitor monitor;

    @Inject
    private TransferProcessObservable observable;

    @Inject
    private ContractAgreementTerminationService contractAgreementTerminationService;

    @Override
    public void initialize(ServiceExtensionContext context) {
        contractAgreementTerminationService.registerListener(new ContractTerminationObserver() {
            @Override
            public void contractTerminationCompletedOnThisInstance(ContractTerminationEvent contractTerminationEvent) {
                val message = new MdsContractTerminationEvent(
                    UuidGenerator.INSTANCE.generate().toString(),
                    contractTerminationEvent.contractAgreementId(),
                    "Contract termination event: terminated contract %s at %s from this EDC. Reason: %s  Detail: %s".formatted(contractTerminationEvent.contractAgreementId(), contractTerminationEvent.timestamp(), contractTerminationEvent.reason(),
                        contractTerminationEvent.detail())
                );
            }

            @Override
            public void contractTerminatedByCounterparty(ContractTerminationEvent contractTerminationEvent) {
                ContractTerminationObserver.super.contractTerminatedByCounterparty(contractTerminationEvent);
            }
        });
    }
}
