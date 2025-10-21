/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.assets_page.services

import de.sovity.edc.ce.api.ui.model.AssetsPageRequest
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.multiset
import de.sovity.edc.ce.api.utils.jooq.ListPageQueryService
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext
import org.jooq.Field
import org.jooq.impl.DSL

@Service
class AssetsPageQueryService(
    private val listPageQueryService: ListPageQueryService,
    private val assetsPageFilterService: AssetsPageFilterService,
    private val assetsPageSortService: AssetsPageSortService
) {
    fun fetchAssetsPage(
        dsl: DSLContext,
        request: AssetsPageRequest
    ): AssetsPageRs {
        val fields = AssetsPageFields()

        return dsl.select(
            AssetsPageRs::assets from {
                getAssetsPageEntries(request, fields)
            },
            AssetsPageRs::count from {
                getTotalAssets(request, fields)
            }
        ).fetchSingleInto(AssetsPageRs::class.java)
    }

    private fun getAssetsPageEntries(
        request: AssetsPageRequest,
        fields: AssetsPageFields
    ): Field<List<AssetsPageEntryRs>> = multiset(
        DSL.select(
            AssetsPageEntryRs::assetId from { fields.asset.ASSET_ID },
            AssetsPageEntryRs::title from { fields.title },
            AssetsPageEntryRs::description from { fields.description },
            AssetsPageEntryRs::dataSourceAvailability from { fields.dataSourceAvailability },
        ).from(fields.asset)
            .where(assetsPageFilterService.applyFilters(request, fields))
            .orderBy(assetsPageSortService.applySort(request, fields))
            .limit(request.pagination?.pageSize)
            .offset(listPageQueryService.getOffset(request.pagination))
    )


    private fun getTotalAssets(
        request: AssetsPageRequest,
        fields: AssetsPageFields
    ): Field<Int> = DSL.field(
        DSL.selectCount()
            .from(fields.asset)
            .where(assetsPageFilterService.applyFilters(request, fields))
    )
}
