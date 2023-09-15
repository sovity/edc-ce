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
import de.sovity.edc.ext.wrapper.utils.EdcPropertyUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.transfer.spi.types.TransferRequest;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class TransferRequestBuilder {

    private final ObjectMapper objectMapper;
    private final ContractAgreementUtils contractAgreementUtils;
    private final ContractNegotiationUtils contractNegotiationUtils;
    private final EdcPropertyUtils edcPropertyUtils;
    private final String connectorId;

    public TransferRequest buildTransferRequest(
            ContractAgreementTransferRequest request
    ) {
        Objects.requireNonNull(request.getType(), "type");
        return switch (request.getType()) {
            case PARAMS_ONLY -> buildTransferRequest(request.getParams());
            case CUSTOM_JSON -> parseTransferRequestJson(request);
        };
    }

    private TransferRequest buildTransferRequest(
            ContractAgreementTransferRequestParams params
    ) {
        var contractId = params.getContractAgreementId();
        var agreement = contractAgreementUtils.findByIdOrThrow(contractId);
        var negotiation = contractNegotiationUtils.findByContractAgreementIdOrThrow(contractId);
        var address = edcPropertyUtils.buildDataAddress(params.getDataSinkProperties());

        return TransferRequest.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .protocol(HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
                .connectorAddress(negotiation.getCounterPartyAddress())
                .connectorId(connectorId)
                .contractId(contractId)
                .assetId(agreement.getAssetId())
                .dataDestination(address)
                .privateProperties(edcPropertyUtils.toMapOfObject(params.getTransferProcessProperties()))
                .callbackAddresses(List.of())
                .build();
    }

    @SneakyThrows
    private TransferRequest parseTransferRequestJson(ContractAgreementTransferRequest request) {
        return objectMapper.readValue(request.getCustomJson(), TransferRequest.class);
    }

}
