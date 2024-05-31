/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services;

import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementDirection;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementTransferProcess;
import de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory.TransferProcessStateService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.utcMillisToOffsetDateTime;
import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.utcSecondsToOffsetDateTime;

@RequiredArgsConstructor
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
            @NonNull List<TransferProcess> transferProcesses
    ) {
        var assetParticipantId = contractNegotiationUtils.getProviderParticipantId(negotiation);
        var assetConnectorEndpoint = contractNegotiationUtils.getProviderConnectorEndpoint(negotiation);

        ContractAgreementCard card = new ContractAgreementCard();
        card.setContractAgreementId(agreement.getId());
        card.setContractNegotiationId(negotiation.getId());
        card.setDirection(ContractAgreementDirection.fromType(negotiation.getType()));
        card.setCounterPartyAddress(negotiation.getCounterPartyAddress());
        card.setCounterPartyId(negotiation.getCounterPartyId());
        card.setContractSigningDate(utcSecondsToOffsetDateTime(agreement.getContractSigningDate()));
        card.setAsset(assetMapper.buildUiAsset(asset, assetConnectorEndpoint, assetParticipantId));
        card.setContractPolicy(policyMapper.buildUiPolicy(agreement.getPolicy()));
        card.setTransferProcesses(buildTransferProcesses(transferProcesses));
        return card;
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
    private ContractAgreementTransferProcess buildContractAgreementTransfer(
            TransferProcess transferProcessEntity) {
        var transferProcess = new ContractAgreementTransferProcess();
        transferProcess.setTransferProcessId(transferProcessEntity.getId());
        transferProcess.setLastUpdatedDate(
                utcMillisToOffsetDateTime(transferProcessEntity.getUpdatedAt()));
        transferProcess.setState(transferProcessStateService.buildTransferProcessState(
                transferProcessEntity.getState()));
        transferProcess.setErrorMessage(transferProcessEntity.getErrorDetail());
        return transferProcess;
    }
}
