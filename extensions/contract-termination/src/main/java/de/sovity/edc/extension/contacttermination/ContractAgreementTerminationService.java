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
import de.sovity.edc.extension.messenger.SovityMessenger;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

import static de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy.COUNTERPARTY;
import static de.sovity.edc.ext.db.jooq.enums.ContractTerminatedBy.SELF;

@RequiredArgsConstructor
public class ContractAgreementTerminationService {

    private final SovityMessenger sovityMessenger;
    private final ContractAgreementTerminationDetailsQuery contractAgreementTerminationDetailsQuery;
    private final TerminateContractQuery terminateContractQuery;
    private final Monitor monitor;
    private final String thisParticipantId;

    /**
     * This is to terminate an EDC's own contract.
     * If the termination comes from an external system, use {@link #terminateCounterpartyAgreement(String, ContractTermination)} to validate the counter-party's identity.
     */
    public OffsetDateTime terminateAgreement(ContractTermination termination) {
        val maybeDetails = contractAgreementTerminationDetailsQuery.fetchAgreementDetails(termination.contractAgreementId());

        if (maybeDetails.isEmpty()) {
            throw new EdcException("Could not find the contract agreement with ID %s.".formatted(termination.contractAgreementId()));
        }

        val details = maybeDetails.get();

        if (details.isTerminated()) {
            return details.terminatedAt();
        }

        val terminatedAt = terminateContractQuery.terminateConsumerAgreement(termination, SELF);
        if (terminatedAt.failed()) {
            throw new EdcException(terminatedAt.getFailureDetail());
        }

        notifyTerminationToProvider(details.counterpartyAddress(), termination);

        return terminatedAt.getContent();
    }

    public Result<OffsetDateTime> terminateCounterpartyAgreement(
        @Nullable String identity,
        ContractTermination termination) {

        val maybeDetails = contractAgreementTerminationDetailsQuery.fetchAgreementDetails(termination.contractAgreementId());

        if (maybeDetails.isEmpty()) {
            val message = "Could not find the contract agreement with ID %s.".formatted(termination.contractAgreementId());
            return Result.failure(message);
        }

        val details = maybeDetails.get();

        boolean isConsumerAndSenderIsProvider = details.isConsumer() && details.providerAgentId().equals(identity);
        boolean isProviderAndSenderIsConsumer = details.isProvider() && details.consumerAgentId().equals(identity);

        if (!(isConsumerAndSenderIsProvider || isProviderAndSenderIsConsumer)) {
            monitor.severe("The EDC %s attempted to terminate a contract that it was not related to!".formatted(details.consumerAgentId()));
            return Result.failure("The requester's identity %s is neither the consumer nor the provider".formatted(identity));
        }

        if (details.isTerminated()) {
            return Result.failure("The contract is already terminated");
        }

        if (thisParticipantId.equals(details.counterpartyId())) {
            return terminateContractQuery.terminateConsumerAgreement(termination, SELF);
        } else {
            return terminateContractQuery.terminateConsumerAgreement(termination, COUNTERPARTY);
        }
    }

    public void notifyTerminationToProvider(String counterPartyAddress, ContractTermination termination) {
        sovityMessenger.send(
            counterPartyAddress,
            new ContractTerminationOutgoingMessage(
                termination.contractAgreementId(),
                termination.detail(),
                termination.reason()));
    }
}
