/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer ISST - initial implementation of an unified workflow for creating data offerings
 *     Fraunhofer ISST - contributions to the Eclipse EDC 0.2.0 migration
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services;

import de.sovity.edc.ce.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ce.api.ui.model.ContractAgreementTerminationInfo;
import de.sovity.edc.ce.api.ui.model.ContractAgreementTransferProcess;
import de.sovity.edc.ce.api.ui.model.ContractTerminatedBy;
import de.sovity.edc.ce.api.ui.pages.transferhistory.TransferProcessStateService;
import de.sovity.edc.ce.db.jooq.tables.records.SovityContractTerminationRecord;
import de.sovity.edc.ce.libs.mappers.AssetMapper;
import de.sovity.edc.ce.libs.mappers.PolicyMapper;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

import static de.sovity.edc.ce.api.ui.model.ContractTerminationStatus.ONGOING;
import static de.sovity.edc.ce.api.ui.model.ContractTerminationStatus.TERMINATED;
import static de.sovity.edc.ce.api.utils.EdcDateUtils.utcMillisToOffsetDateTime;
import static de.sovity.edc.ce.api.utils.EdcDateUtils.utcSecondsToOffsetDateTime;

@RequiredArgsConstructor
@Service
public class ContractAgreementPageCardBuilder {
    private final PolicyMapper policyMapper;
    private final TransferProcessStateService transferProcessStateService;
    private final AssetMapper assetMapper;
    private final ContractNegotiationUtils contractNegotiationUtils;

    @NotNull
    public ContractAgreementCard buildContractAgreementCard(
        @NonNull ContractAgreement agreement,
        @NonNull ContractNegotiation negotiation,
        @NonNull Asset asset,
        @NonNull List<TransferProcess> transferProcesses,
        SovityContractTerminationRecord termination
    ) {

        var assetParticipantId = contractNegotiationUtils.getProviderParticipantId(negotiation);
        var assetConnectorEndpoint = contractNegotiationUtils.getProviderConnectorEndpoint(negotiation);

        ContractAgreementCard card = new ContractAgreementCard();
        card.setContractAgreementId(agreement.getId());
        card.setContractNegotiationId(negotiation.getId());
        ContractNegotiation.Type type = negotiation.getType();
        card.setDirection(ContractAgreementDirectionUtils.fromType(type));
        card.setCounterPartyAddress(negotiation.getCounterPartyAddress());
        card.setCounterPartyId(negotiation.getCounterPartyId());
        card.setContractSigningDate(utcSecondsToOffsetDateTime(agreement.getContractSigningDate()));
        card.setAsset(assetMapper.buildUiAsset(asset, assetConnectorEndpoint, assetParticipantId));
        card.setContractPolicy(policyMapper.buildUiPolicy(agreement.getPolicy()));
        card.setTransferProcesses(buildTransferProcesses(transferProcesses));

        addTermination(termination, card);

        return card;
    }

    private static void addTermination(SovityContractTerminationRecord termination, ContractAgreementCard card) {
        if (termination != null) {
            card.setTerminationStatus(TERMINATED);
            card.setTerminationInformation(ContractAgreementTerminationInfo.builder()
                .detail(termination.getDetail())
                .reason(termination.getReason())
                .terminatedAt(termination.getTerminatedAt())
                .terminatedBy(switch (termination.getTerminatedBy()) {
                    case SELF -> ContractTerminatedBy.SELF;
                    case COUNTERPARTY -> ContractTerminatedBy.COUNTERPARTY;
                })
                .build());
        } else {
            card.setTerminationStatus(ONGOING);
            card.setTerminationInformation(null);
        }
    }

    @NotNull
    private List<ContractAgreementTransferProcess> buildTransferProcesses(
        @NonNull List<TransferProcess> transferProcessEntities
    ) {
        return transferProcessEntities.stream()
            .map(this::buildContractAgreementTransfer)
            .sorted(Comparator.comparing(ContractAgreementTransferProcess::getLastUpdatedDate)
                .reversed())
            .toList();
    }

    @NotNull
    private ContractAgreementTransferProcess buildContractAgreementTransfer(TransferProcess transferProcessEntity) {

        var transferProcess = new ContractAgreementTransferProcess();

        transferProcess.setTransferProcessId(transferProcessEntity.getId());
        transferProcess.setLastUpdatedDate(utcMillisToOffsetDateTime(transferProcessEntity.getUpdatedAt()));
        transferProcess.setState(transferProcessStateService.buildTransferProcessState(transferProcessEntity.getState()));
        transferProcess.setErrorMessage(transferProcessEntity.getErrorDetail());

        return transferProcess;
    }
}
