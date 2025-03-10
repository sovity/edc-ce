/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination;

import de.sovity.edc.ce.modules.messaging.contract_termination.query.ContractAgreementTerminationDetailsQuery;
import de.sovity.edc.ce.modules.messaging.contract_termination.query.TerminateContractQuery;
import de.sovity.edc.ce.modules.messaging.messenger.SovityMessage;
import de.sovity.edc.ce.modules.messaging.messenger.SovityMessenger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.observe.Observable;
import org.eclipse.edc.spi.observe.ObservableImpl;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.function.Consumer;

import static de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy.SELF;

@RequiredArgsConstructor
public class ContractAgreementTerminationService {

    private final SovityMessenger sovityMessenger;
    private final ContractAgreementTerminationDetailsQuery contractAgreementTerminationDetailsQuery;
    private final TerminateContractQuery terminateContractQuery;
    private final Monitor monitor;
    private final String thisParticipantId;
    @Getter
    private final Observable<ContractTerminationObserver> contractTerminationObservable = new ObservableImpl<>();

    /**
     * This is to terminate an EDC's own contract.
     * If the termination comes from an external system, use
     * {@link #terminateAgreementAsCounterparty(DSLContext, String, ContractTerminationParam)}
     * to validate the counter-party's identity.
     */
    public OffsetDateTime terminateAgreementOrThrow(DSLContext dsl, ContractTerminationParam termination) {

        val starterEvent = ContractTerminationEvent.from(termination, OffsetDateTime.now(), thisParticipantId);
        notifyObservers(it -> it.contractTerminationStartedFromThisInstance(starterEvent));

        val details = contractAgreementTerminationDetailsQuery.fetchAgreementDetailsOrThrow(dsl, termination.contractAgreementId());

        if (details == null) {
            throw new EdcException("Could not find the contract agreement with ID %s.".formatted(termination.contractAgreementId()));
        }

        if (details.isTerminated()) {
            return details.terminatedAt();
        }

        val terminatedAt = terminateContractQuery.terminateConsumerAgreementOrThrow(dsl, termination, SELF);

        val endEvent = ContractTerminationEvent.from(termination, terminatedAt, thisParticipantId);
        notifyObservers(it -> it.contractTerminationCompletedOnThisInstance(endEvent));

        notifyTerminationToProvider(details.counterpartyAddress(), details.counterpartyId(), termination);

        return terminatedAt;
    }

    public OffsetDateTime terminateAgreementAsCounterparty(
        DSLContext dsl,
        @Nullable String identity,
        ContractTerminationParam termination
    ) {
        val starterEvent = ContractTerminationEvent.from(termination, OffsetDateTime.now(), null);
        notifyObservers(it -> it.contractTerminatedByCounterpartyStarted(starterEvent));

        val details = contractAgreementTerminationDetailsQuery.fetchAgreementDetailsOrThrow(dsl, termination.contractAgreementId());

        if (details == null) {
            val message = "Could not find the contract agreement with ID %s.".formatted(termination.contractAgreementId());
            throw new EdcException(message);
        }

        boolean thisEdcIsConsumerAndSenderIsProvider = details.thisEdcIsTheConsumer() && details.providerAgentId().equals(identity);
        boolean thisEdcIsProviderAndSenderIsConsumer = details.thisEdcIsTheProvider() && details.consumerAgentId().equals(identity);

        if (!(thisEdcIsConsumerAndSenderIsProvider || thisEdcIsProviderAndSenderIsConsumer)) {
            monitor.severe(
                "The EDC %s attempted to terminate a contract that it was not related to!".formatted(details.consumerAgentId()));
            throw new EdcException("The requester's identity %s is neither the consumer nor the provider".formatted(identity));
        }

        if (details.isTerminated()) {
            throw new EdcException("The contract is already terminated");
        }

        val agent = thisParticipantId.equals(details.counterpartyId()) ? SELF : COUNTERPARTY;

        val result = terminateContractQuery.terminateConsumerAgreementOrThrow(dsl, termination, agent);

        val endEvent = ContractTerminationEvent.from(termination, OffsetDateTime.now(), details.counterpartyId());
        notifyObservers(it -> it.contractTerminatedByCounterparty(endEvent));

        return result;
    }

    public void notifyTerminationToProvider(
        String counterPartyAddress,
        String counterPartyId,
        ContractTerminationParam termination
    ) {

        val notificationEvent = ContractTerminationEvent.from(termination, OffsetDateTime.now(), null);
        notifyObservers(it -> it.contractTerminationOnCounterpartyStarted(notificationEvent));

        sovityMessenger.send(
            SovityMessage.class,
            counterPartyAddress,
            counterPartyId,
            new ContractTerminationMessage(
                termination.contractAgreementId(),
                termination.detail(),
                termination.reason()));
    }

    private void notifyObservers(Consumer<ContractTerminationObserver> call) {
        for (val listener : contractTerminationObservable.getListeners()) {
            try {
                call.accept(listener);
            } catch (Exception e) {
                monitor.warning("Failure when notifying the contract termination listener.", e);
            }
        }
    }
}
