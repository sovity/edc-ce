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


package de.sovity.edc.extension.contractagreementtransferapi.service;

import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.TransferType;
import org.eclipse.edc.spi.types.domain.DataAddress;

import java.util.HashMap;
import java.util.UUID;

public class DataRequestService {

    private final ContractAgreementService contractAgreementService;
    private final ContractNegotiationByAgreementService contractNegotiationByAgreementService;

    public DataRequestService(
            ContractAgreementService contractAgreementService,
            ContractNegotiationByAgreementService contractNegotiationByAgreementService) {
        this.contractAgreementService = contractAgreementService;
        this.contractNegotiationByAgreementService = contractNegotiationByAgreementService;
    }

    public DataRequest buildDataRequest(String contractAgreementId, DataAddress dataAddress) {
        var contractAgreement = contractAgreementService.findById(contractAgreementId);
        var contractNegotiation =
                contractNegotiationByAgreementService.getNegotiationForAgreementId(contractAgreementId);
        return DataRequest.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .assetId(removeDuplicatedPrefixIfPresent(contractAgreement.getAssetId()))
                .connectorId("consumer")
                .dataDestination(dataAddress)
                .destinationType(dataAddress.getType())
                .connectorAddress(contractNegotiation.getCounterPartyAddress())
                .contractId(contractAgreementId)
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
}
