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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.extension.contacttermination.ContractAgreementTerminationService;
import de.sovity.edc.extension.contacttermination.ContractTerminationEvent;
import de.sovity.edc.extension.contacttermination.ContractTerminationObserver;
import lombok.val;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.event.EventEnvelope;
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
    private ContractAgreementTerminationService contractAgreementTerminationService;

    private ObjectMapper objectMapper;

    @Override
    public void initialize(ServiceExtensionContext context) {

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        contractAgreementTerminationService.getContractTerminationObservable()
            .registerListener(new ContractTerminationObserver() {
                @Override
                public void contractTerminationCompletedOnThisInstance(ContractTerminationEvent contractTerminationEvent) {
                    val logEntry = LogEntry.from("contractTerminatedByThisInstance", contractTerminationEvent);

                    sendMessage(contractTerminationEvent, logEntry);
                }

                @Override
                public void contractTerminatedByCounterparty(ContractTerminationEvent contractTerminationEvent) {
                    val logEntry = LogEntry.from("contractTerminatedByCounterparty", contractTerminationEvent);
                    sendMessage(contractTerminationEvent, logEntry);
                }
            });
    }

    private void sendMessage(ContractTerminationEvent contractTerminationEvent, LogEntry logEntry) {
        try {
            val message = objectMapper.writeValueAsString(logEntry);
            val event = new MdsContractTerminationEvent(
                UuidGenerator.INSTANCE.generate().toString(),
                contractTerminationEvent.contractAgreementId(),
                message
            );

            @SuppressWarnings("unchecked")
            EventEnvelope.Builder<MdsContractTerminationEvent> builder = EventEnvelope.Builder.newInstance();

            val eventEnvelope = builder
                .at(System.currentTimeMillis())
                .payload(event)
                .build();

            eventRouter.publish(eventEnvelope);

            monitor.debug("Published event for " + logEntry);
        } catch (JsonProcessingException e) {
            monitor.warning("Failed to serialize the event for the logging house " + logEntry);
        }
    }
}
