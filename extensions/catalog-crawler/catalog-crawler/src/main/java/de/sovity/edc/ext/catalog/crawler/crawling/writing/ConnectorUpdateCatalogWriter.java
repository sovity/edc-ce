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

package de.sovity.edc.ext.catalog.crawler.crawling.writing;

import de.sovity.edc.ext.catalog.crawler.crawling.fetching.model.FetchedDataOffer;
import de.sovity.edc.ext.catalog.crawler.crawling.logging.ConnectorChangeTracker;
import de.sovity.edc.ext.catalog.crawler.dao.CatalogPatchApplier;
import de.sovity.edc.ext.catalog.crawler.dao.connectors.ConnectorRef;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.DSLContext;

import java.util.Collection;

@RequiredArgsConstructor
public class ConnectorUpdateCatalogWriter {
    private final CatalogPatchBuilder catalogPatchBuilder;
    private final CatalogPatchApplier catalogPatchApplier;

    /**
     * Updates a connector's data offers with given {@link FetchedDataOffer}s.
     *
     * @param dsl dsl
     * @param connectorRef connector
     * @param fetchedDataOffers fetched data offers
     * @param changes change tracker for log message
     */
    @SneakyThrows
    public void updateDataOffers(
            DSLContext dsl,
            ConnectorRef connectorRef,
            Collection<FetchedDataOffer> fetchedDataOffers,
            ConnectorChangeTracker changes
    ) {
        var patch = catalogPatchBuilder.buildDataOfferPatch(dsl, connectorRef, fetchedDataOffers);
        changes.setNumOffersAdded(patch.dataOffers().getInsertions().size());
        changes.setNumOffersUpdated(patch.dataOffers().getUpdates().size());
        changes.setNumOffersDeleted(patch.dataOffers().getDeletions().size());
        catalogPatchApplier.applyDbUpdatesBatched(dsl, patch);
    }
}
