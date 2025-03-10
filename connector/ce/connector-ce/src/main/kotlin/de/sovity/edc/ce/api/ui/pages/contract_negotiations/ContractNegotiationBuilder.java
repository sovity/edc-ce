/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_negotiations;


import de.sovity.edc.ce.api.ui.model.ContractNegotiationRequest;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractRequest;
import org.eclipse.edc.protocol.dsp.http.spi.types.HttpMessageProtocol;


@RequiredArgsConstructor
@Service
public class ContractNegotiationBuilder {

    private final ContractOfferMapper contractOfferMapper;

    public ContractRequest buildContractNegotiation(ContractNegotiationRequest request) {
        var counterPartyAddress = request.getCounterPartyAddress();
        var contractOffer = contractOfferMapper.buildContractOffer(request);

        return ContractRequest.Builder.newInstance()
            .counterPartyAddress(counterPartyAddress)
            .protocol(HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
            .contractOffer(contractOffer)
            .build();
    }
}
