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

package de.sovity.edc.ext.catalog.crawler.crawling.fetching;

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedCatalog;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedContractOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.utils.catalog.model.DspCatalog;
import de.sovity.edc.utils.catalog.model.DspContractOffer;
import de.sovity.edc.utils.catalog.model.DspDataOffer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class FetchedCatalogBuilder {
    private final FetchedCatalogMappingUtils fetchedCatalogMappingUtils;

    public FetchedCatalog buildFetchedCatalog(DspCatalog catalog, ConnectorRef connectorRef) {
        assertEqualEndpoint(catalog, connectorRef);
        assertEqualParticipantId(catalog, connectorRef);

        var fetchedDataOffers = catalog.getDataOffers().stream()
                .map(dspDataOffer -> buildFetchedDataOffer(dspDataOffer, connectorRef))
                .toList();

        var fetchedCatalog = new FetchedCatalog();
        fetchedCatalog.setConnectorRef(connectorRef);
        fetchedCatalog.setDataOffers(fetchedDataOffers);
        return fetchedCatalog;
    }

    private void assertEqualParticipantId(DspCatalog catalog, ConnectorRef connectorRef) {
        Validate.isTrue(
                connectorRef.getConnectorId().equals(catalog.getParticipantId()),
                String.format(
                        "Connector connectorId does not match the participantId: connectorId %s, participantId %s",
                        connectorRef.getConnectorId(),
                        catalog.getParticipantId()
                )
        );
    }

    private void assertEqualEndpoint(DspCatalog catalog, ConnectorRef connectorRef) {
        Validate.isTrue(
                connectorRef.getEndpoint().equals(catalog.getEndpoint()),
                String.format(
                        "Connector endpoint mismatch: expected %s, got %s",
                        connectorRef.getEndpoint(),
                        catalog.getEndpoint()
                )
        );
    }

    @NotNull
    private FetchedDataOffer buildFetchedDataOffer(
            DspDataOffer dspDataOffer,
            ConnectorRef connectorRef
    ) {
        var uiAsset = fetchedCatalogMappingUtils.buildUiAsset(dspDataOffer, connectorRef);
        var uiAssetJson = fetchedCatalogMappingUtils.buildUiAssetJson(uiAsset);

        var fetchedDataOffer = new FetchedDataOffer();
        fetchedDataOffer.setAssetId(uiAsset.getAssetId());
        fetchedDataOffer.setUiAsset(uiAsset);
        fetchedDataOffer.setUiAssetJson(uiAssetJson);
        fetchedDataOffer.setContractOffers(buildFetchedContractOffers(dspDataOffer.getContractOffers()));
        return fetchedDataOffer;
    }

    @NotNull
    private List<FetchedContractOffer> buildFetchedContractOffers(List<DspContractOffer> offers) {
        return offers.stream()
                .map(this::buildFetchedContractOffer)
                .toList();
    }

    @NotNull
    private FetchedContractOffer buildFetchedContractOffer(DspContractOffer offer) {
        var uiPolicyJson = fetchedCatalogMappingUtils.buildUiPolicyJson(offer.getPolicyJsonLd());
        var contractOffer = new FetchedContractOffer();
        contractOffer.setContractOfferId(offer.getContractOfferId());
        contractOffer.setUiPolicyJson(uiPolicyJson);
        return contractOffer;
    }

}
