/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.assets_page

import de.sovity.edc.ce.api.ui.model.AssetsPageRequest
import de.sovity.edc.ce.api.ui.model.AssetsPageResult
import de.sovity.edc.ce.api.ui.pages.assets_page.services.AssetsPageBuilder
import de.sovity.edc.ce.api.ui.pages.assets_page.services.AssetsPageQueryService
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.DSLContext

@Service
class AssetsPageApiService(
    private val assetsPageQueryService: AssetsPageQueryService,
    private val assetsPageBuilder: AssetsPageBuilder,
) {
    fun assetsPage(
        dsl: DSLContext,
        request: AssetsPageRequest
    ): AssetsPageResult {
        val assetsPage = assetsPageQueryService.fetchAssetsPage(dsl, request)
        return assetsPageBuilder.buildAssetsPage(assetsPage, request.pagination)
    }
}
