/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.assets_page.services

import de.sovity.edc.ce.api.ui.model.AssetsPageRequest
import de.sovity.edc.ce.api.ui.model.AssetsPageSortProperty
import de.sovity.edc.ce.api.utils.jooq.ListPageQueryService
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.SortField

@Service
class AssetsPageSortService(
    private val listPageQueryService: ListPageQueryService
) {
    fun applySort(
        request: AssetsPageRequest,
        fields: AssetsPageFields
    ): List<SortField<out Any>> = request.sortBy?.map {
        val field = when (it.field) {
            AssetsPageSortProperty.TITLE -> fields.title
            AssetsPageSortProperty.DESCRIPTION -> fields.description
        }
        listPageQueryService.withSortDirection(field, it.direction)
    } ?: emptyList()

}
