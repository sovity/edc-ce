/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.asset_detail_page.services

import de.sovity.edc.ce.api.utils.jooq.JooqUtilsSovity.from
import de.sovity.edc.ce.db.jooq.Tables
import de.sovity.edc.ce.db.jooq.tables.EdcAsset
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext

@Service
class AssetDetailPageQueryService {
    fun fetchAssetDetailPage(
        dsl: DSLContext,
        assetId: String
    ): AssetDetailPageRs? {
        val a = Tables.EDC_ASSET
        return select(dsl, a)
            .where(a.ASSET_ID.eq(assetId))
            .fetchOneInto(AssetDetailPageRs::class.java)
    }

    fun fetchAssetDetailPages(
        dsl: DSLContext
    ): List<AssetDetailPageRs> {
        val a = Tables.EDC_ASSET
        return select(dsl, a)
            .fetchInto(AssetDetailPageRs::class.java)
    }

    private fun select(
        dsl: DSLContext,
        a: EdcAsset
    ) = dsl.select(
        AssetDetailPageRs::assetId from { a.ASSET_ID },
        AssetDetailPageRs::createdAt from { a.CREATED_AT },
        AssetDetailPageRs::properties from { a.PROPERTIES },
        AssetDetailPageRs::privateProperties from { a.PRIVATE_PROPERTIES },
        AssetDetailPageRs::dataAddress from { a.DATA_ADDRESS }
    ).from(a)
}
