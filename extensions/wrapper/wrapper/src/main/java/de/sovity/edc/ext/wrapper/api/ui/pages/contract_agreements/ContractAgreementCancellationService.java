/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements;

import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.extension.contactcancellation.query.AgreementDetails;
import de.sovity.edc.extension.contactcancellation.query.AgreementDetailsQuery;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ContractAgreementCancellationService {

    private final AgreementDetailsQuery agreementDetailsQuery;

    public IdResponseDto cancelContractAgreement(String contractAgreementId) {
        // TODO: check existence of the contract agreement
        Optional<AgreementDetails> agreementDetails = agreementDetailsQuery.fetchAgreementDetails(contractAgreementId);

        // TODO: detect in which direction the contract is acting

        // TODO: return the contract agreement ID if succesful
        return null;
    }
}
