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
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractRequest;
import org.eclipse.edc.protocol.dsp.http.spi.types.HttpMessageProtocol;


@RequiredArgsConstructor
public class ContractNegotiationBuilder {

    private final ContractOfferMapper contractOfferMapper;

    public ContractRequest buildContractNegotiation(ContractNegotiationRequest request) {
        var counterPartyAddress = request.getCounterPartyAddress();

        return ContractRequest.Builder.newInstance()
            .counterPartyAddress(counterPartyAddress)
            // TODO: deprecated and moved into the contractOffer.policy.assigner, set by `.contractOffer()` below
            //  git diff v0.5.1..v0.6.0 -- spi/control-plane/contract-spi/src/main/java/org/eclipse/edc/connector/contract/spi/types/negotiation/ContractRequest.java
            //  This should come automatically from the parsing of the Policy's json and needs testing
            //  This is also questioning migrations: how do we know that the policy contains the provider id?
            // .providerId(request.getCounterPartyParticipantId())
            .protocol(HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP)
            .contractOffer(contractOfferMapper.buildContractOffer(request))
            .build();
    }
}
