/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.assets

import de.sovity.edc.ce.api.common.model.UiAsset
import de.sovity.edc.ce.api.ui.pages.asset_detail_page.services.AssetDetailPageBuilder
import de.sovity.edc.ce.api.ui.pages.asset_detail_page.services.AssetDetailPageQueryService
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext

@Service
class UseCaseAssetApiService(
    private val assetDetailPageQueryService: AssetDetailPageQueryService,
    private val assetDetailPageBuilder: AssetDetailPageBuilder
) {
    fun getAssets(dsl: DSLContext): List<UiAsset> {
        // We re-use the asset detail page, which is the UiAsset only right now
        val assetDetailPages = assetDetailPageQueryService.fetchAssetDetailPages(dsl)
        return assetDetailPages.map { assetDetailPageBuilder.buildUiAsset(it) }
    }
}
