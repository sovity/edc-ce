

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


import com.fasterxml.jackson.core.JsonProcessingException;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationRequest;
import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.CriterionMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractRequest;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;

import de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils.ContractOfferMapper;


@RequiredArgsConstructor
public class ContractNegotiationBuilder {

    private final ContractOfferMapper contractOfferMapper;

    public ContractRequest buildContractNegotiation(ContractNegotiationRequest request) {
        var protocol = request.getProtocol();
        var counterPartyAddress = request.getCounterPartyAddress();

        return ContractRequest.Builder.newInstance()
                .counterPartyAddress(counterPartyAddress)
                .protocol(protocol)
                .contractOffer(contractOfferMapper.buildContractOffer(request))
                .build();
    }
}
