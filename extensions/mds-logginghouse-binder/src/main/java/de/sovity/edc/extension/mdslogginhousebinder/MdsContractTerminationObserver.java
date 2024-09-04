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

package de.sovity.edc.extension.mdslogginhousebinder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.contacttermination.ContractTerminationEvent;
import de.sovity.edc.extension.contacttermination.ContractTerminationObserver;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.event.EventEnvelope;
import org.eclipse.edc.spi.event.EventRouter;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.uuid.UuidGenerator;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class MdsContractTerminationObserver implements ContractTerminationObserver {

    private final EventRouter eventRouter;

    private final Monitor monitor;

    private final ObjectMapper objectMapper;

    @Override
    public void contractTerminationCompletedOnThisInstance(ContractTerminationEvent contractTerminationEvent) {
        sendMessage("contractTerminatedByThisInstance", contractTerminationEvent);
    }

    @Override
    public void contractTerminatedByCounterparty(ContractTerminationEvent contractTerminationEvent) {
        sendMessage("contractTerminatedByCounterparty", contractTerminationEvent);
    }

    private void sendMessage(String logEvent, ContractTerminationEvent contractTerminationEvent) {
        val logEntry = LogEntry.from(logEvent, contractTerminationEvent);
        try {
            val event = buildLogEvent(contractTerminationEvent.contractAgreementId(), logEntry);
            publishEvent(event);
            monitor.debug("Published event for " + logEntry);
        } catch (JsonProcessingException e) {
            val message = "Failed to serialize the event for the LoggingHouse " + logEntry;
            throw new LoggingHouseException(message, e);
        }
    }

    private @NotNull MdsContractTerminationEvent buildLogEvent(String contractAgreementId, LogEntry logEntry)
        throws JsonProcessingException {
        val message = objectMapper.writeValueAsString(logEntry);
        return new MdsContractTerminationEvent(
            UuidGenerator.INSTANCE.generate().toString(),
            contractAgreementId,
            message
        );
    }

    private void publishEvent(MdsContractTerminationEvent event) {
        @SuppressWarnings("unchecked")
        EventEnvelope.Builder<MdsContractTerminationEvent> builder = EventEnvelope.Builder.newInstance();

        val eventEnvelope = builder
            .at(System.currentTimeMillis())
            .payload(event)
            .build();

        eventRouter.publish(eventEnvelope);
    }
}
