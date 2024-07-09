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
import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import de.sovity.edc.utils.catalog.DspCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;

@RequiredArgsConstructor
public class FetchedCatalogService {
    private final DspCatalogService dspCatalogService;
    private final FetchedCatalogBuilder catalogPatchBuilder;

    /**
     * Fetches {@link ContractOffer}s and de-duplicates them into {@link FetchedDataOffer}s.
     *
     * @param connectorRef connector
     * @return updated connector db row
     */
    @SneakyThrows
    public FetchedCatalog fetchCatalog(ConnectorRef connectorRef) {
        var dspCatalog = dspCatalogService.fetchDataOffers(connectorRef.getEndpoint());
        return catalogPatchBuilder.buildFetchedCatalog(dspCatalog, connectorRef);
    }
}
