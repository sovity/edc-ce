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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.spi.EdcException;

import java.util.Optional;

@RequiredArgsConstructor
public class ContractAgreementUtils {

    private final ContractAgreementService contractAgreementService;

    public ContractAgreement findByIdOrThrow(String contractAgreementId) {
        return Optional.ofNullable(contractAgreementService.findById(contractAgreementId))
                .orElseThrow(() -> new EdcException("Could not fetch contractNegotiation for " +
                        "contractAgreement"));
    }
}
