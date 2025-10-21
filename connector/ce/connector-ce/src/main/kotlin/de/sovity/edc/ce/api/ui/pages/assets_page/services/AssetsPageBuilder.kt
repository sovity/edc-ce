/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.assets_page.services

import de.sovity.edc.ce.api.common.model.PaginationRequest
import de.sovity.edc.ce.api.ui.model.AssetsPageEntry
import de.sovity.edc.ce.api.ui.model.AssetsPageResult
import de.sovity.edc.ce.api.utils.jooq.ListPageBuilder
import de.sovity.edc.ce.libs.mappers.asset.AssetJsonLdParser
import de.sovity.edc.ce.libs.mappers.asset.utils.ShortDescriptionBuilder
import de.sovity.edc.runtime.simple_di.Service

@Service
class AssetsPageBuilder(
    private val listPageBuilder: ListPageBuilder,
    private val shortDescriptionBuilder: ShortDescriptionBuilder,
    private val assetJsonLdParser: AssetJsonLdParser
) {

    fun buildAssetsPage(
        listPageRs: AssetsPageRs,
        pagination: PaginationRequest?
    ): AssetsPageResult {
        val content = listPageRs.assets.map { buildAssetsPageEntry(it) }
        return AssetsPageResult.builder()
            .assets(content)
            .pagination(
                listPageBuilder.buildPagination(
                    contentSize = content.size,
                    totalItems = listPageRs.count,
                    pagination = pagination
                )
            )
            .build()
    }

    private fun buildAssetsPageEntry(
        asset: AssetsPageEntryRs
    ): AssetsPageEntry {
        return AssetsPageEntry.builder()
            .assetId(asset.assetId)
            .title(asset.title ?: asset.assetId)
            .descriptionShortText(shortDescriptionBuilder.buildShortDescription(asset.description))
            .dataSourceAvailability(assetJsonLdParser.getDataSourceAvailability(asset.dataSourceAvailability))
            .build()
    }
}
