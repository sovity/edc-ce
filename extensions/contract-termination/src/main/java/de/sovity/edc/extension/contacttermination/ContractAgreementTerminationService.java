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

package de.sovity.edc.extension.contacttermination;

import de.sovity.edc.extension.contacttermination.query.ContractAgreementTerminationDetailsQuery;
import de.sovity.edc.extension.contacttermination.query.TerminateContractQuery;
import de.sovity.edc.extension.messenger.SovityMessage;
import de.sovity.edc.extension.messenger.SovityMessenger;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.observe.Observable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy.SELF;

@RequiredArgsConstructor
public class ContractAgreementTerminationService implements Observable<ContractTerminationObserver> {

    private final SovityMessenger sovityMessenger;
    private final ContractAgreementTerminationDetailsQuery contractAgreementTerminationDetailsQuery;
    private final TerminateContractQuery terminateContractQuery;
    private final Monitor monitor;
    private final String thisParticipantId;
    private final List<WeakReference<ContractTerminationObserver>> contractTerminationObservers =
        Collections.synchronizedList(new ArrayList<>());
    private final ReentrantLock observersLock = new ReentrantLock();

    /**
     * This is to terminate an EDC's own contract.
     * If the termination comes from an external system, use
     * {@link #terminateCounterpartyAgreement(DSLContext, String, ContractTerminationParam)}
     * to validate the counter-party's identity.
     */
    public OffsetDateTime terminateAgreementOrThrow(DSLContext dsl, ContractTerminationParam termination) {

        notifyObservers(ContractTerminationObserver::contractTerminationStartedFromThisInstance);

        val details = contractAgreementTerminationDetailsQuery.fetchAgreementDetailsOrThrow(dsl, termination.contractAgreementId());

        if (details == null) {
            throw new EdcException("Could not find the contract agreement with ID %s.".formatted(termination.contractAgreementId()));
        }

        if (details.isTerminated()) {
            return details.terminatedAt();
        }

        val terminatedAt = terminateContractQuery.terminateConsumerAgreementOrThrow(dsl, termination, SELF);

        notifyTerminationToProvider(details.counterpartyAddress(), termination);


        notifyObservers(it -> it.contractTerminationCompletedOnThisInstance(terminatedAt));

        return terminatedAt;
    }

    public OffsetDateTime terminateCounterpartyAgreement(
        DSLContext dsl,
        @Nullable String identity,
        ContractTerminationParam termination
    ) {
        notifyObservers(ContractTerminationObserver::contractTerminatedByCounterpartyStarted);

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

        notifyObservers(ContractTerminationObserver::contractTerminatedByCounterparty);

        return result;
    }

    public void notifyTerminationToProvider(String counterPartyAddress, ContractTerminationParam termination) {

        notifyObservers(ContractTerminationObserver::contractTerminationOnCounterpartyStarted);

        val future = sovityMessenger.send(
            SovityMessage.class,
            counterPartyAddress,
            new ContractTerminationMessage(
                termination.contractAgreementId(),
                termination.detail(),
                termination.reason()));

        future.thenAccept(it -> {
            notifyObservers(ContractTerminationObserver::contractTerminationCompletedOnCounterpartyInstance);
        });
    }

    @Override
    public Collection<ContractTerminationObserver> getListeners() {
        return contractTerminationObservers.stream().filter(it -> it.get() != null).toList().stream().map(Reference::get).toList();
    }

    @Override
    public void registerListener(ContractTerminationObserver listener) {
        try {
            observersLock.lock();

            final var refreshed = getFilteredWeakReferences(null);

            contractTerminationObservers.clear();
            contractTerminationObservers.addAll(refreshed);
            contractTerminationObservers.add(new WeakReference<>(listener));
        } finally {
            observersLock.unlock();
        }
    }

    @Override
    public void unregisterListener(ContractTerminationObserver listener) {
        try {
            observersLock.lock();

            final var refreshed = getFilteredWeakReferences(listener);

            contractTerminationObservers.clear();
            contractTerminationObservers.addAll(refreshed);
        } finally {
            observersLock.unlock();
        }
    }

    private @NotNull List<WeakReference<ContractTerminationObserver>> getFilteredWeakReferences(ContractTerminationObserver listener) {
        return contractTerminationObservers.stream().filter(it -> {
            val obs = it.get();
            if (obs == null) {
                return false;
            }
            return obs != listener;
        }).toList();
    }

    private void notifyObservers(Consumer<ContractTerminationObserver> call) {
        for (val weakRef : contractTerminationObservers) {
            try {
                val observer = weakRef.get();
                if (observer != null) {
                    call.accept(observer);
                }
            } catch (Exception e) {
                monitor.warning("Failure when notifying contract termination observer.");
            }
        }
    }
}
