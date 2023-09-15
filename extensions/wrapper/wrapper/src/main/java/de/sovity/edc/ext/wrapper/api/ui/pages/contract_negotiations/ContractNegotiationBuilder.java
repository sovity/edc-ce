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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations;


import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationRequest;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractRequest;
import org.eclipse.edc.protocol.dsp.spi.types.HttpMessageProtocol;


@RequiredArgsConstructor
public class ContractNegotiationBuilder {

    private final ContractOfferMapper contractOfferMapper;

    public ContractRequest buildContractNegotiation(ContractNegotiationRequest request) {
        var counterPartyAddress = request.getCounterPartyAddress();

        return ContractRequest.Builder.newInstance()
                .counterPartyAddress(counterPartyAddress)
                .providerId(request.getCounterPartyParticipantId())
                .protocol(HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
                .contractOffer(contractOfferMapper.buildContractOffer(request))
                .build();
    }
}
