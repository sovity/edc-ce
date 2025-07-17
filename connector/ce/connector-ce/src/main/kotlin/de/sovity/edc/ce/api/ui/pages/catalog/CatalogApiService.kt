/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.catalog

import de.sovity.edc.ce.api.ui.model.UiDataOffer
import de.sovity.edc.ce.api.utils.notFoundError
import de.sovity.edc.ce.libs.mappers.dsp.DspCatalogService
import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop
import org.eclipse.edc.spi.query.Criterion
import org.eclipse.edc.spi.query.QuerySpec

@Service
class CatalogApiService(
    private val uiDataOfferBuilder: UiDataOfferBuilder,
    private val dspCatalogService: DspCatalogService
) {
    fun fetchDataOffers(participantId: String, connectorEndpoint: String): List<UiDataOffer> {
        val dspCatalog = dspCatalogService.fetchDataOffers(participantId, connectorEndpoint)
        return uiDataOfferBuilder.buildUiDataOffers(dspCatalog)
    }

    fun fetchDataOffer(
        participantId: String,
        connectorEndpoint: String,
        assetId: String
    ): UiDataOffer {
        val querySpec = QuerySpec.max().toBuilder()
            .filter(
                Criterion.criterion(
                    "'" + Prop.Edc.ID + "'",
                    "=",
                    assetId
                )
            )
            .build()
        val dspCatalog = dspCatalogService.fetchDataOffersWithFilters(
            participantId,
            connectorEndpoint,
            querySpec
        )
        return uiDataOfferBuilder.buildUiDataOffers(dspCatalog).firstOrNull()
            ?: notFoundError("No data offer found")
    }
}
