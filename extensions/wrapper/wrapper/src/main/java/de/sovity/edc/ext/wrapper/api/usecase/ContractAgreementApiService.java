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

package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.ext.wrapper.api.usecase.model.ContractAgreementDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractAgreementResult;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ContractAgreementApiService {
    private final AssetIndex assetIndex;
    private final ContractAgreementService contractAgreementService;

    public ContractAgreementResult contractAgreementEndpoint() {
        var querySpec = QuerySpec.Builder.newInstance().build();
        var contractAgreementDtos = new ArrayList<ContractAgreementDto>();
        var contractAgreements = contractAgreementService.query(querySpec).getContent().toList();

        for (var contractAgreement : contractAgreements) {
            var asset = assetIndex.findById(contractAgreement.getAssetId());
            var policy = contractAgreement.getPolicy();

            //TODO: contract negotiation details of contractagreement
            //TODO: transfer history of contractagreement

            contractAgreementDtos.add(new ContractAgreementDto(contractAgreement, List.of(asset), List.of(policy)));
        }

        return new ContractAgreementResult(contractAgreementDtos);
    }
}
