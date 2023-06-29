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

package de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.spi.contractnegotiation.ContractNegotiationService;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.List;

@RequiredArgsConstructor
public class ContractNegotiationUtils {

    private final ContractNegotiationService contractNegotiationService;

    public ContractNegotiation findByContractAgreementIdOrThrow(String contractAgreementId) {
        var querySpec = QuerySpec.Builder.newInstance()
                .filter(List.of(new Criterion("contractAgreement.id", "=", contractAgreementId)))
                .build();
        return contractNegotiationService.query(querySpec).getContent()
                .findFirst()
                .orElseThrow(() -> new EdcException("Could not fetch contractNegotiation for " +
                        "contractAgreement"));
    }
}
