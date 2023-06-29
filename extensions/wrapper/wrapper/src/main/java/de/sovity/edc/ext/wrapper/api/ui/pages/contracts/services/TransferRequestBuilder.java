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

package de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementTransferRequestParams;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementTransferRequestType;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.ContractAgreementUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.ContractNegotiationUtils;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.TransformerRegistryUtils;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.api.management.asset.model.DataAddressDto;
import org.eclipse.edc.connector.api.management.transferprocess.model.TransferRequestDto;
import org.eclipse.edc.connector.transfer.spi.types.TransferType;
import org.eclipse.edc.spi.types.domain.DataAddress;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class TransferRequestBuilder {

    private final ObjectMapper objectMapper;
    private final ContractAgreementUtils contractAgreementUtils;
    private final ContractNegotiationUtils contractNegotiationUtils;
    private final TransformerRegistryUtils transformerRegistryUtils;

    public TransferRequestDto buildTransferRequestDto(
            ContractAgreementTransferRequest request
    ) {
        Objects.requireNonNull(request.getType(), "type");
        return switch (request.getType()) {
            case PARAMS_ONLY -> buildTransferRequestDto(request.getParams());
            case CUSTOM_JSON -> parseTransferRequestDtoJson(request);
            default -> throw new IllegalArgumentException("Unhandled %s: %s".formatted(
                    ContractAgreementTransferRequestType.class.getSimpleName(),
                    request.getType()
            ));
        };
    }

    private TransferRequestDto buildTransferRequestDto(
            ContractAgreementTransferRequestParams params
    ) {
        var contractId = params.getContractAgreementId();
        var agreement = contractAgreementUtils.findByIdOrThrow(contractId);
        var negotiation = contractNegotiationUtils.findByContractAgreementIdOrThrow(contractId);
        var address = buildDataAddress(params.getDataSinkProperties());

        var transferType = TransferType.Builder.transferType()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .isFinite(true)
                .build();

        return TransferRequestDto.Builder.newInstance()
                .connectorAddress(negotiation.getCounterPartyAddress())
                .id(UUID.randomUUID().toString())
                .contractId(contractId)
                .dataDestination(address)
                .managedResources(false)
                .properties(params.getProperties())
                .transferType(transferType)
                .protocol("ids-multipart")
                .connectorId("consumer")
                .assetId(agreement.getAssetId())
                .build();
    }

    @SneakyThrows
    private TransferRequestDto parseTransferRequestDtoJson(ContractAgreementTransferRequest request) {
        return objectMapper.readValue(request.getCustomJson(), TransferRequestDto.class);
    }

    @SneakyThrows
    private DataAddress buildDataAddress(Map<String, String> dataAddressProperties) {
        var dto = DataAddressDto.Builder.newInstance().properties(dataAddressProperties).build();
        return transformerRegistryUtils.transformOrThrow(dto, DataAddress.class);
    }
}
