/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements;

import de.sovity.edc.ext.wrapper.api.ui.model.ContractTerminationRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.extension.contacttermination.ContractAgreementTerminationService;
import de.sovity.edc.extension.contacttermination.ContractTerminationParam;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.EdcException;
import org.jooq.DSLContext;

@RequiredArgsConstructor
public class ContractAgreementTerminationApiService {

    private final ContractAgreementTerminationService contractAgreementTerminationService;

    public IdResponseDto terminate(
        DSLContext dsl,
        String contractAgreementId,
        ContractTerminationRequest contractTerminationRequest) {

        try {
            val terminatedAt = contractAgreementTerminationService.terminateAgreementOrThrow(
                dsl,
                new ContractTerminationParam(
                    contractAgreementId,
                    contractTerminationRequest.getDetail(),
                    contractTerminationRequest.getReason()));

            return IdResponseDto.builder()
                .id(contractAgreementId)
                .lastUpdatedDate(terminatedAt)
                .build();

        } catch (RuntimeException e) {
            throw new EdcException("Failed to terminate the agreement %s".formatted(contractAgreementId) + " : " + e.getMessage(), e);
        }
    }
}
