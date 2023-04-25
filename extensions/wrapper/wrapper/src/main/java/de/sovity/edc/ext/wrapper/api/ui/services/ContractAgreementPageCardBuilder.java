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

package de.sovity.edc.ext.wrapper.api.ui.services;

import de.sovity.edc.ext.wrapper.api.ui.model.AssetDto;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementDirection;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementTransfer;
import de.sovity.edc.ext.wrapper.api.ui.model.PolicyDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.utcMillisToOffsetDateTime;
import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.utcSecondsToOffsetDateTime;
import static de.sovity.edc.ext.wrapper.utils.MapUtils.mapValues;

@RequiredArgsConstructor
public class ContractAgreementPageCardBuilder {
    private final TransferProcessStateService transferProcessStateService;

    @NotNull
    public ContractAgreementCard buildContractAgreementCard(
            @NonNull ContractAgreement agreement,
            @NonNull ContractNegotiation negotiation,
            @NonNull Asset asset,
            @NonNull List<TransferProcess> transferProcesses
    ) {
        ContractAgreementCard card = new ContractAgreementCard();
        card.setContractAgreementId(agreement.getId());
        card.setDirection(ContractAgreementDirection.fromType(negotiation.getType()));
        card.setCounterPartyAddress(negotiation.getCounterPartyAddress());
        card.setCounterPartyId(negotiation.getCounterPartyId());
        card.setContractSigningDate(utcSecondsToOffsetDateTime(agreement.getContractSigningDate()));
        card.setContractStartDate(utcSecondsToOffsetDateTime(agreement.getContractStartDate()));
        card.setContractEndDate(utcSecondsToOffsetDateTime(agreement.getContractEndDate()));
        card.setAsset(buildAssetDto(asset));
        card.setContractPolicy(buildPolicyDto(agreement.getPolicy()));
        card.setTransferProcesses(buildTransferProcesses(transferProcesses));
        return card;
    }

    @NotNull
    private List<ContractAgreementTransfer> buildTransferProcesses(
            @NonNull List<TransferProcess> transferProcesses
    ) {
        return transferProcesses.stream()
                .map(this::buildContractAgreementTransfer)
                .sorted(Comparator.comparing(ContractAgreementTransfer::getLastUpdatedDate).reversed())
                .toList();
    }

    @NotNull
    private ContractAgreementTransfer buildContractAgreementTransfer(TransferProcess transferProcess) {
        var dto = new ContractAgreementTransfer();
        dto.setTransferProcessId(transferProcess.getId());
        dto.setLastUpdatedDate(utcMillisToOffsetDateTime(transferProcess.getUpdatedAt()));
        dto.setState(transferProcessStateService.buildTransferStateInfo(transferProcess.getState()));
        dto.setErrorMessage(transferProcess.getErrorDetail());
        return dto;
    }

    @NotNull
    private PolicyDto buildPolicyDto(@NonNull Policy policy) {
        return new PolicyDto(policy);
    }

    @NotNull
    private AssetDto buildAssetDto(@NonNull Asset asset) {
        var createdAt = utcMillisToOffsetDateTime(asset.getCreatedAt());
        var properties = mapValues(asset.getProperties(), Object::toString);
        return new AssetDto(asset.getId(), createdAt, properties);
    }
}
