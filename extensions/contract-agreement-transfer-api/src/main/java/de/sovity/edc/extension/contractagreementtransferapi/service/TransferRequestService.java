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
import org.eclipse.edc.connector.transfer.spi.types.TransferRequest;
import org.eclipse.edc.spi.types.domain.DataAddress;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TransferRequestService {

    private final ContractAgreementService contractAgreementService;
    private final ContractNegotiationByAgreementService contractNegotiationByAgreementService;

    public TransferRequestService(
            ContractAgreementService contractAgreementService,
            ContractNegotiationByAgreementService contractNegotiationByAgreementService) {
        this.contractAgreementService = contractAgreementService;
        this.contractNegotiationByAgreementService = contractNegotiationByAgreementService;
    }

    public TransferRequest buildTransferRequest(String contractAgreementId,
                                                DataAddress dataAddress) {
        var contractAgreement = contractAgreementService.findById(contractAgreementId);
        var contractNegotiation =
                contractNegotiationByAgreementService.getNegotiationForAgreementId(contractAgreementId);
        var dataRequest = DataRequest.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .assetId(removeDuplicatedPrefixIfPresent(contractAgreement.getAssetId()))
                .connectorId("consumer")
                .dataDestination(dataAddress)
                .destinationType(dataAddress.getType())
                .connectorAddress(contractNegotiation.getCounterPartyAddress())
                .contractId(contractAgreementId)
                .managedResources(false)
                .protocol("ids-multipart")
                .build();
        return TransferRequest.Builder.newInstance()
                .callbackAddresses(List.of())
                .privateProperties(new HashMap<>())
                .dataRequest(dataRequest)
                .build();
    }

    private String removeDuplicatedPrefixIfPresent(String assetId) {
        return assetId.startsWith("urn:artifact:urn:artifact:") ?
                assetId.substring("urn:artifact:".length()) : assetId;
    }
}
