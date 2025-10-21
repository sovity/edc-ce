/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.assets_page.services

import de.sovity.edc.ce.api.ui.model.AssetsPageRequest
import de.sovity.edc.ce.api.utils.jooq.SearchUtils
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.Condition

@Service
class AssetsPageFilterService {
    fun applyFilters(
        request: AssetsPageRequest,
        fields: AssetsPageFields
    ): Condition = applySearch(request.searchText, fields)

    private fun applySearch(
        searchText: String?,
        fields: AssetsPageFields
    ): Condition = SearchUtils.simpleSearch(
        searchText, listOf(
            fields.id,
            fields.title,
            fields.description,
        )
    )
}
