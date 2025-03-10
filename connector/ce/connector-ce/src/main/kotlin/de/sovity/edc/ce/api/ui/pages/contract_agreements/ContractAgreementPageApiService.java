/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements;

import de.sovity.edc.ce.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ce.api.ui.model.ContractAgreementPage;
import de.sovity.edc.ce.api.ui.model.ContractAgreementPageQuery;
import de.sovity.edc.ce.api.ui.pages.contract_agreements.services.ContractAgreementDataFetcher;
import de.sovity.edc.ce.api.ui.pages.contract_agreements.services.ContractAgreementPageCardBuilder;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import java.util.Comparator;

@RequiredArgsConstructor
@Service
public class ContractAgreementPageApiService {
    private final ContractAgreementDataFetcher contractAgreementDataFetcher;
    private final ContractAgreementPageCardBuilder contractAgreementPageCardBuilder;

    @NotNull
    public ContractAgreementPage contractAgreementPage(DSLContext dsl, @Nullable ContractAgreementPageQuery contractAgreementPageQuery) {
        var agreements = contractAgreementDataFetcher.getContractAgreements(dsl);

        var cards = agreements.stream()
            .map(agreement -> contractAgreementPageCardBuilder.buildContractAgreementCard(
                agreement.agreement(),
                agreement.negotiation(),
                agreement.asset(),
                agreement.transfers(),
                agreement.termination()))
            .sorted(Comparator.comparing(ContractAgreementCard::getContractSigningDate).reversed());

        if (contractAgreementPageQuery == null || contractAgreementPageQuery.getTerminationStatus() == null) {
            return new ContractAgreementPage(cards.toList());
        } else {
            var filtered = cards.filter(card ->
                    card.getTerminationStatus().equals(contractAgreementPageQuery.getTerminationStatus()))
                .toList();
            return new ContractAgreementPage(filtered);
        }
    }

    public ContractAgreementCard contractAgreement(DSLContext dsl, String contractAgreementId) {
        val agreementData = contractAgreementDataFetcher.getContractAgreement(dsl, contractAgreementId);
        return contractAgreementPageCardBuilder.buildContractAgreementCard(
            agreementData.agreement(),
            agreementData.negotiation(),
            agreementData.asset(),
            agreementData.transfers(),
            agreementData.termination()
        );
    }
}
