/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils.jooq

import de.sovity.edc.ce.api.common.model.PaginationRequest
import de.sovity.edc.ce.api.common.model.PaginationResult
import de.sovity.edc.runtime.simple_di.Service
import kotlin.math.ceil
import kotlin.math.max

@Service
class ListPageBuilder {
    fun buildPagination(
        contentSize: Int,
        totalItems: Int,
        pagination: PaginationRequest?,
    ): PaginationResult {
        val pageSize = pagination?.pageSize ?: contentSize
        val currentPage = pagination?.pageOneBased ?: 1
        val totalPages = ceil(totalItems.toDouble() / pageSize).toInt()
        return PaginationResult().also {
            it.totalItems = totalItems
            it.lastPage = max(totalPages, 1)
            it.previousPage = if (currentPage > 1) currentPage - 1 else null
            it.currentPage = currentPage
            it.nextPage = if (currentPage < it.lastPage) currentPage + 1 else null
            it.pageSize = pageSize
            it.pageStart = if (contentSize == 0) 0 else currentPage.minus(1) * pageSize + 1
            it.pageEnd = if (contentSize == 0) 0 else it.pageStart + contentSize - 1
        }
    }
}
