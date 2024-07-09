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
 */

package de.sovity.edc.extension.contacttermination;

import de.sovity.edc.extension.contacttermination.query.ContractAgreementTerminationDetailsQuery;
import de.sovity.edc.extension.contacttermination.query.TerminateContractQuery;
import de.sovity.edc.extension.messenger.SovityMessenger;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.spi.result.Result;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
public class ContractAgreementTerminationService {

    private final SovityMessenger sovityMessenger;
    private final ContractAgreementTerminationDetailsQuery contractAgreementTerminationDetailsQuery;
    private final TerminateContractQuery terminateContractQuery;

    public Result<OffsetDateTime> terminateContractAgreementForConsumerAndProvider(ContractTermination termination) {
        val maybeDetails = contractAgreementTerminationDetailsQuery.fetchAgreementDetails(termination.contractAgreementId());

        if (maybeDetails.isEmpty()) {
            val message = "Could not find the contract agreement with ID %s.".formatted(termination.contractAgreementId());
            return Result.failure(message);
        }

        val details = maybeDetails.get();

        if (details.isTerminated()) {
            return Result.failure("The contract is already terminated");
        }

        val terminatedAt = terminateContractQuery.terminateConsumerAgreement(termination);

        notifyTerminationToProvider(details.counterpartyAddress(), termination);

        return Result.success(terminatedAt);

    }

    public Result<OffsetDateTime> secureTerminateContractAgreement(@Nullable String identity, ContractTermination termination) {

        val maybeDetails = contractAgreementTerminationDetailsQuery.fetchAgreementDetails(termination.contractAgreementId());

        if (maybeDetails.isEmpty()) {
            val message = "Could not find the contract agreement with ID %s.".formatted(termination.contractAgreementId());
            return Result.failure(message);
        }

        val details = maybeDetails.get();

        boolean isConsumerAndSenderIsProvider = details.type().equals(ContractNegotiation.Type.CONSUMER) && details.providerAgentId().equals(identity);
        boolean isProviderAndSenderIsConsumer = details.type().equals(ContractNegotiation.Type.PROVIDER) && details.consumerAgentId().equals(identity);
        if (!(isConsumerAndSenderIsProvider || isProviderAndSenderIsConsumer)) {
            // TODO: logging
            return Result.failure("The requester's identity %s is neither the consumer nor the provider".formatted(identity));
        }

        if (details.isTerminated()) {
            return Result.failure("The contract is already terminated");
        }

        val terminatedAt = terminateContractQuery.terminateConsumerAgreement(termination);

        return Result.success(terminatedAt);
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
