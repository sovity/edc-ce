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

import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPageQuery;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementPageCardBuilder;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.contract.spi.ContractId;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.service.spi.result.ServiceResult;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import java.util.Comparator;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ContractAgreementPageApiService {
    private final ContractAgreementDataFetcher contractAgreementDataFetcher;
    private final ContractAgreementPageCardBuilder contractAgreementPageCardBuilder;
    private final AssetService assetService;
    private final ContractAgreementService contractAgreementService;

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

        val asset = Tables.EDC_ASSET;
        val assetProperty = Tables.EDC_ASSET_PROPERTY;
        val contractAgreement = Tables.EDC_CONTRACT_AGREEMENT;
        val contractNegotiation = Tables.EDC_CONTRACT_NEGOTIATION;

        val contractId = ContractId.parseId(contractAgreementId).orElseThrow(f -> new RuntimeException(f.getFailureDetail()));

        val availability = assetProperty.as("availability");
        val title = assetProperty.as("title");
        val creatorOrganizationName = assetProperty.as("creatorOrganizationName");

        val query =
            dsl.select(
                    contractNegotiation.AGREEMENT_ID.as("whatever"),
                    contractNegotiation.ID,
                    contractNegotiation.TYPE,
                    contractNegotiation.COUNTERPARTY_ADDRESS,
                    contractNegotiation.COUNTERPARTY_ID,
                    asset.ASSET_ID,
                    contractNegotiation.COUNTERPARTY_ADDRESS,
                    contractNegotiation.COUNTERPARTY_ID,
                    availability.PROPERTY_VALUE.as("availability"),
                    title.PROPERTY_VALUE.as("title"),
                    creatorOrganizationName.PROPERTY_VALUE.as("creatorOrganizationName")
                )
                .from(contractNegotiation)
                .join(contractAgreement).on(contractNegotiation.AGREEMENT_ID.eq(contractAgreement.AGR_ID))
                .join(asset).on(asset.ASSET_ID.eq(contractId.assetIdPart()))
                .leftJoin(availability)
                .on(
                    asset.ASSET_ID.eq(availability.ASSET_ID_FK)
                        .and(availability.PROPERTY_NAME.eq(Prop.SovityDcatExt.DATA_SOURCE_AVAILABILITY))
                )
                .leftJoin(title)
                .on(
                    asset.ASSET_ID.eq(title.ASSET_ID_FK)
                        .and(title.PROPERTY_NAME.eq(Prop.Dcterms.TITLE))
                )
                .leftJoin(creatorOrganizationName)
                .on(
                    asset.ASSET_ID.eq(creatorOrganizationName.ASSET_ID_FK)
                        .and(creatorOrganizationName.PROPERTY_NAME.eq(Prop.Dcterms.CREATOR))
                )
                .where(contractNegotiation.AGREEMENT_ID.eq(contractAgreementId));

        val queryResult = query.fetchOne();

        if (queryResult == null) {
            throw new EdcException("No contract agreement with id %s".formatted(contractAgreementId));
        }

        val queriedAsset = assetService.query(QuerySpec.Builder.newInstance()
                .filter(Criterion.criterion(Asset.PROPERTY_ID, "=", contractId.assetIdPart()))
                .build())
            .getContent()
            .findFirst()
            .get();

        val queriedAgreement = contractAgreementService.query(QuerySpec.Builder.newInstance()
                .filter(Criterion.criterion("", "", ""))
                .build());

        return new ContractAgreementCard(
            contractAgreementId,
            queryResult.get(contractNegotiation.ID),
            queryResult.get(contractNegotiation.TYPE),
            queryResult.get(contractNegotiation.COUNTERPARTY_ADDRESS),
            queryResult.get(contractNegotiation.COUNTERPARTY_ID),
            queryResult.get(contractNegotiation.COUNTERPARTY_ID),
            queryResult.get(contractAgreement.SIGNING_DATE),
            queriedAsset,
            queriedAgreement,

        );
    }
}
