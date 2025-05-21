/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.catalog

import de.sovity.edc.ce.api.common.model.CatalogQueryV2
import de.sovity.edc.ce.api.common.model.CatalogSortByDirection
import de.sovity.edc.ce.api.ui.model.UiDataOffer
import de.sovity.edc.ce.api.ui.pages.catalog.UiDataOfferBuilder
import de.sovity.edc.ce.libs.mappers.dsp.DspCatalogService
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.spi.query.QuerySpec
import org.eclipse.edc.spi.query.SortOrder

@Service
class UseCaseCatalogV2ApiService(
    private val uiDataOfferBuilder: UiDataOfferBuilder,
    private val dspCatalogService: DspCatalogService,
    private val assetFilterMapper: AssetFilterMapper
) {

    fun fetchDataOffers(query: CatalogQueryV2): List<UiDataOffer> {
        val querySpec = buildQuerySpec(query)
        val dspCatalog = dspCatalogService.fetchDataOffersWithFilters(
            query.participantId,
            query.connectorEndpoint,
            querySpec
        )
        return uiDataOfferBuilder.buildUiDataOffers(dspCatalog)
    }

    private fun buildQuerySpec(query: CatalogQueryV2): QuerySpec {
        val builder = QuerySpec.Builder.newInstance()
        builder.limit(query.limit)
        builder.offset(query.offset)
        query.sortBy?.let {
            builder.sortField(QuerySpecUtils.encodeAssetPropertyPath(it.assetPropertyPath))
            builder.sortOrder(if (it.direction == CatalogSortByDirection.DESC) SortOrder.DESC else SortOrder.ASC)
        }
        builder.filter(assetFilterMapper.buildCriteria(query.filter))
        return builder.build()
    }
}
