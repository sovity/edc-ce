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
import de.sovity.edc.ce.modules.messaging.messenger.SovityMessenger;
import lombok.Getter;
import lombok.val;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.observe.Observable;
import org.eclipse.edc.spi.observe.ObservableImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.ce.db.jooq.enums.ContractTerminatedBy.SELF;

public class ContractAgreementTerminationService {
    private final int threadPoolSize;
    private final SovityMessenger sovityMessenger;
    private final ContractAgreementTerminationDetailsQuery contractAgreementTerminationDetailsQuery;
    private final TerminateContractQuery terminateContractQuery;
    private final Monitor monitor;
    private final String thisParticipantId;
    @Getter
    private final Observable<ContractTerminationObserver> contractTerminationObservable = new ObservableImpl<>();
    private ExecutorService executor;

    public ContractAgreementTerminationService(int threadPoolSize, SovityMessenger sovityMessenger, ContractAgreementTerminationDetailsQuery contractAgreementTerminationDetailsQuery, TerminateContractQuery terminateContractQuery, Monitor monitor, String thisParticipantId) {
        this.threadPoolSize = threadPoolSize;
        this.sovityMessenger = sovityMessenger;
        this.contractAgreementTerminationDetailsQuery = contractAgreementTerminationDetailsQuery;
        this.terminateContractQuery = terminateContractQuery;
        this.monitor = monitor;
        this.thisParticipantId = thisParticipantId;
        this.executor = cachedThreadPoolExecutorWithMaxSize(threadPoolSize);
    }

    private static @NotNull ThreadPoolExecutor cachedThreadPoolExecutorWithMaxSize(int threadPoolSize) {
        return new ThreadPoolExecutor(
            0,
            threadPoolSize,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadFactory() {

                private static final AtomicInteger index = new AtomicInteger();

                @Override
                public Thread newThread(@NotNull Runnable r) {
                    val t = new Thread(r);
                    t.setDaemon(true);
                    t.setName("ContractAgreementTerminationThread-" + index.getAndIncrement());
                    return t;
                }
            });
    }

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

        val result = sovityMessenger.send(
            ContractTerminationMessage.class,
            counterPartyAddress,
            counterPartyId,
            new ContractTerminationMessage(
                termination.contractAgreementId(),
                termination.detail(),
                termination.reason()));

        executor.submit(() -> {
            try {
                val r = result.get();
                if (r.failed()) {
                    monitor.warning("Failed to notify the counterparty about the termination: " + r.getFailureDetail());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                monitor.warning("ExecutionException occurred while notifying a contract termination to a counterparty", e);
            }
        });
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
