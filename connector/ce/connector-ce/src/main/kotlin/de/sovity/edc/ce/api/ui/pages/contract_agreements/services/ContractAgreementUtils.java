/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services;

import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.spi.EdcException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ContractAgreementUtils {

    private final ContractAgreementService contractAgreementService;

    public ContractAgreement findByIdOrThrow(String contractAgreementId) {
        return Optional.ofNullable(contractAgreementService.findById(contractAgreementId))
            .orElseThrow(() -> new EdcException("Could not fetch contractNegotiation for " +
                "contractAgreement"));
    }
}
