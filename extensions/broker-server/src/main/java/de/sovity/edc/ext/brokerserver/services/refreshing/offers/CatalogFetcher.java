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

package de.sovity.edc.ext.brokerserver.services.refreshing.offers;

import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedCatalog;
import de.sovity.edc.ext.brokerserver.services.refreshing.offers.model.FetchedDataOffer;
import de.sovity.edc.utils.catalog.DspCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;

@RequiredArgsConstructor
public class CatalogFetcher {
    private final DspCatalogService dspCatalogService;
    private final FetchedCatalogBuilder fetchedCatalogBuilder;

    /**
     * Fetches {@link ContractOffer}s and de-duplicates them into {@link FetchedDataOffer}s.
     *
     * @param connectorEndpoint connector endpoint
     * @return updated connector db row
     */
    @SneakyThrows
    public FetchedCatalog fetchCatalog(String connectorEndpoint) {
        var dspCatalog = dspCatalogService.fetchDataOffers(connectorEndpoint);
        return fetchedCatalogBuilder.buildFetchedCatalog(dspCatalog);
    }
}
