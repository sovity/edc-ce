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

import de.sovity.edc.ext.wrapper.api.ServiceException;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.SelfDescriptionService;
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
    private final SelfDescriptionService selfDescriptionService;

    public ContractNegotiation findByContractAgreementIdOrThrow(String contractAgreementId) {
        var querySpec = QuerySpec.Builder.newInstance()
                .filter(List.of(new Criterion("contractAgreement.id", "=", contractAgreementId)))
                .build();
        return contractNegotiationService.query(querySpec).orElseThrow(ServiceException::new)
                .findFirst()
                .orElseThrow(() -> new EdcException("Could not fetch contractNegotiation for " +
                        "contractAgreement"));
    }

    /**
     * Return's the asset provider's connector endpoint
     *
     * @param negotiation negotiation
     * @return participant ID
     */
    public String getProviderConnectorEndpoint(ContractNegotiation negotiation) {
        if (negotiation.getType() == ContractNegotiation.Type.PROVIDER) {
            return selfDescriptionService.getConnectorEndpoint();
        }

        return negotiation.getCounterPartyAddress();
    }

    /**
     * Return's the asset provider's participant ID
     *
     * @param negotiation negotiation
     * @return participant ID
     */
    public String getProviderParticipantId(ContractNegotiation negotiation) {
        if (negotiation.getType() == ContractNegotiation.Type.PROVIDER) {
            return selfDescriptionService.getParticipantId();
        }

        return negotiation.getCounterPartyId();
    }
}
