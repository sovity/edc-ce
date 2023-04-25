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

package de.sovity.edc.ext.wrapper.api.ui.services;

import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPage;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@RequiredArgsConstructor
public class ContractAgreementPageService {
    private final ContractAgreementDataFetcher contractAgreementDataFetcher;
    private final ContractAgreementPageCardBuilder contractAgreementPageCardBuilder;

    @NotNull
    public ContractAgreementPage contractAgreementPage() {
        var agreements = contractAgreementDataFetcher.getContractAgreements();

        var cards = agreements.stream()
                .map(agreement -> contractAgreementPageCardBuilder.buildContractAgreementCard(
                        agreement.agreement(), agreement.negotiation(), agreement.asset(), agreement.transfers()))
                .sorted(Comparator.comparing(ContractAgreementCard::getContractSigningDate).reversed())
                .toList();

        return new ContractAgreementPage(cards);
    }
}
