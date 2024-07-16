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
import de.sovity.edc.extension.db.directaccess.DslContextFactory;
import de.sovity.edc.extension.messenger.SovityMessenger;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

import static de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy.SELF;

@RequiredArgsConstructor
public class ContractAgreementTerminationService {

    private final SovityMessenger sovityMessenger;
    private final DslContextFactory dslContextFactory;
    private final ContractAgreementTerminationDetailsQuery contractAgreementTerminationDetailsQuery;
    private final TerminateContractQuery terminateContractQuery;
    private final Monitor monitor;
    private final String thisParticipantId;

    /**
     * This is to terminate an EDC's own contract.
     * If the termination comes from an external system, use {@link #terminateCounterpartyAgreement(String, ContractTerminationParam)} to validate the counter-party's identity.
     */
    public OffsetDateTime terminateAgreement(ContractTerminationParam termination) {

        val terminatedAt = dslContextFactory.newDslContext().transactionResult(trx -> {
            val details =
                contractAgreementTerminationDetailsQuery.fetchAgreementDetailsOrThrow(trx.dsl(), termination.contractAgreementId());

            if (details == null) {
                throw new EdcException("Could not find the contract agreement with ID %s.".formatted(termination.contractAgreementId()));
            }

            if (details.isTerminated()) {
                return details.terminatedAt();
            }

            val terminated = terminateContractQuery.terminateConsumerAgreementOrThrow(trx.dsl(), termination, SELF);

            notifyTerminationToProvider(details.counterpartyAddress(), termination);

            return terminated;
        });

        return terminatedAt;
    }

    public OffsetDateTime terminateCounterpartyAgreement(
        @Nullable String identity,
        ContractTerminationParam termination) {

        return dslContextFactory.newDslContext().transactionResult((trx) -> {
            val details =
                contractAgreementTerminationDetailsQuery.fetchAgreementDetailsOrThrow(trx.dsl(), termination.contractAgreementId());

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

            return terminateContractQuery.terminateConsumerAgreementOrThrow(trx.dsl(), termination, agent);
        });
    }

    public void notifyTerminationToProvider(String counterPartyAddress, ContractTerminationParam termination) {
        sovityMessenger.send(
            counterPartyAddress,
            new ContractTerminationMessage(
                termination.contractAgreementId(),
                termination.detail(),
                termination.reason()));
    }
}
