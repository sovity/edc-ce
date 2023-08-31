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

package de.sovity.edc.ext.wrapper.api.ui.pages.contracts;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationDto;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.ContractNegotiationBuilder;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class ContractNegotiationApiService {
    private final ContractNegotiationService contractNegotiationService;
    private final ContractNegotiationBuilder contractNegotiationBuilder;

    @NotNull
    public ContractNegotiationDto initiateContractNegotiation(ContractNegotiationRequest request) {
        var contractRequest = contractNegotiationBuilder.buildContractNegotiation(request);
        var contractNegotiation = contractNegotiationService.initiateNegotiation(contractRequest);
        var contractNegotiationStatus = contractNegotiationService.getState(contractNegotiation.getId());
        return new ContractNegotiationDto(contractNegotiation.getId(), contractNegotiation.getCreatedAt(), contractNegotiation.getContractAgreement().getId(), contractNegotiationStatus);
    }

    @NotNull
    public ContractNegotiationDto getContractNegotiation(String contractNegotiationId) {
        var contractNegotiation = contractNegotiationService.findbyId(contractNegotiationId);
        var contractNegotiationStatus = contractNegotiationService.getState(contractNegotiation.getId());
        return new ContractNegotiationDto(contractNegotiation.getId(), contractNegotiation.getCreatedAt(), contractNegotiation.getContractAgreement().getId(), contractNegotiationStatus);
    }
}
