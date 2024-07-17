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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements;

import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPageQuery;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementPageCardBuilder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import java.util.Comparator;

@RequiredArgsConstructor
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
}
