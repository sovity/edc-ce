/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.extension.contractagreementtransferapi.controller;

import org.eclipse.edc.api.transformer.DtoTransformer;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.TransferType;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TransferRequestDtoToDataRequestTransformer implements DtoTransformer<ContractAgreementTransferRequestDto, DataRequest> {

    private final ContractNegotiationService contractNegotiationService;
    private final ContractAgreementService contractAgreementService;

    public TransferRequestDtoToDataRequestTransformer(
            ContractNegotiationService contractNegotiationService,
            ContractAgreementService contractAgreementService) {
        this.contractNegotiationService = contractNegotiationService;
        this.contractAgreementService = contractAgreementService;
    }

    @Override
    public Class<ContractAgreementTransferRequestDto> getInputType() {
        return ContractAgreementTransferRequestDto.class;
    }

    @Override
    public Class<DataRequest> getOutputType() {
        return DataRequest.class;
    }

    @Override
    public @Nullable DataRequest transform(@Nullable ContractAgreementTransferRequestDto transferRequestDto,
                                           @NotNull TransformerContext context) {
        Objects.requireNonNull(context);
        if (transferRequestDto == null) {
            return null;
        }

        var id = Objects.requireNonNullElseGet(transferRequestDto.getId(),
                () -> UUID.randomUUID().toString());
        var contractAgreementId = transferRequestDto.getContractAgreementId();
        var contractAgreement = contractAgreementService.findById(contractAgreementId);
        var contractNegotiation = getNegotiationForAgreementId(contractAgreementId);
        return DataRequest.Builder.newInstance()
                .id(id)
                .assetId(removeDuplicatedPrefixIfPresent(contractAgreement.getAssetId()))
                .connectorId("consumer")
                .dataDestination(transferRequestDto.getDataDestination())
                .destinationType(transferRequestDto.getDataDestination().getType())
                .connectorAddress(contractNegotiation.getCounterPartyAddress())
                .contractId(transferRequestDto.getContractAgreementId())
                .transferType(TransferType.Builder.transferType().isFinite(true).build())
                .properties(new HashMap<>())
                .managedResources(false)
                .protocol("ids-multipart")
                .build();
    }

    private String removeDuplicatedPrefixIfPresent(String assetId) {
        return assetId.startsWith("urn:artifact:urn:artifact:") ?
                assetId.substring("urn:artifact:".length()) : assetId;
    }

    private ContractNegotiation getNegotiationForAgreementId(String contractAgreementId) {
        var querySpec = QuerySpec.Builder.newInstance()
                .filter(List.of(new Criterion("contractAgreement.id", "=", contractAgreementId)))
                .build();
        return contractNegotiationService.query(querySpec).getContent()
                .findFirst()
                .orElseThrow(() -> new EdcException("Could not fetch contractNegotiation for " +
                        "contractAgreement"));
    }
}
