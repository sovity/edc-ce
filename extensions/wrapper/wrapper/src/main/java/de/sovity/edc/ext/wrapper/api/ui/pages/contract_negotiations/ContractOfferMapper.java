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


import de.sovity.edc.ext.wrapper.api.common.mappers.PolicyMapper;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationRequest;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;


@RequiredArgsConstructor
public class ContractOfferMapper {
    private final PolicyMapper policyMapper;

    public ContractOffer buildContractOffer(ContractNegotiationRequest contractRequest) {
        return ContractOffer.Builder.newInstance()
                .id(contractRequest.getContractOfferId())
                .policy(policyMapper.buildPolicy(contractRequest.getPolicyJsonLd()))
                .assetId(contractRequest.getAssetId())
                .build();
    }
}
