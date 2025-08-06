/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils

import de.sovity.edc.ce.api.common.model.TableFilter
import de.sovity.edc.ce.api.common.model.TablePage
import de.sovity.edc.ce.api.utils.jooq.SearchUtils
import org.jooq.Field
import org.jooq.Record
import org.jooq.SelectJoinStep
import kotlin.math.ceil
import kotlin.math.max

fun <SortableProperties : Enum<*>, R : Record> SelectJoinStep<R>.filterTable(
    filter: TableFilter<SortableProperties>,
    searchableFields: List<Field<String?>>,
    extractSortField: (SortableProperties) -> Field<*>,
) = this.queryTable(filter, searchableFields).sortTable(filter, extractSortField).paginateTable(filter)


fun <T, C : TablePage<T>> buildTablePage(
    clazz: Class<C>,
    content: List<T>,
    totalItems: Int,
    currentPage: Int,
    optionalPageSize: Int?
): C {
    val instance = clazz.getDeclaredConstructor().newInstance().also {
        val pageSize = optionalPageSize ?: content.size
        val totalPages = ceil(totalItems.toDouble() / pageSize).toInt()
        it.content = content
        it.totalItems = totalItems
        it.lastPage = max(totalPages - 1, 0)
        it.previousPage = if (currentPage > 0) currentPage - 1 else null
        it.currentPage = currentPage
        it.nextPage = if (currentPage < it.lastPage) currentPage + 1 else null
        it.pageSize = pageSize
        it.pageStart = if (content.isEmpty()) 0 else currentPage * pageSize + 1
        it.pageEnd = if (content.isEmpty()) 0 else it.pageStart + content.size - 1
    }
    return instance
}

fun <SortableProperties : Enum<*>, R : Record> SelectJoinStep<R>.queryTable(
    filter: TableFilter<SortableProperties>,
    searchableFields: List<Field<String?>>,
): SelectJoinStep<R> {
    this.where(
        SearchUtils.simpleSearch(
            filter.query, searchableFields
        )
    )

    return this
}

private fun <SortableProperties : Enum<*>, R : Record> SelectJoinStep<R>.sortTable(
    filter: TableFilter<SortableProperties>,
    extractSortField: (SortableProperties) -> Field<*>,
): SelectJoinStep<R> {
    filter.sort?.forEach {
        val sortField = extractSortField(it.columnName)
        this.orderBy(if (it.isDescending) sortField.desc() else sortField.asc())
    }

    return this
}

private fun <SortableProperties : Enum<*>, R : Record> SelectJoinStep<R>.paginateTable(
    filter: TableFilter<SortableProperties>,
): SelectJoinStep<R> {
    val pageSize = filter.pageSize
    if (pageSize != null) {
        this.limit(pageSize).offset(pageSize * filter.page)
    }

    return this
}
