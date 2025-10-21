/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils.jooq

import de.sovity.edc.ce.api.common.model.PaginationRequest
import de.sovity.edc.ce.api.common.model.SortByDirection
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.Field
import org.jooq.SortField

@Service
class ListPageQueryService {

    fun <F> withSortDirection(
        field: Field<F>,
        direction: SortByDirection?
    ): SortField<F> = if (direction == SortByDirection.DESCENDING) {
        field.desc()
    } else {
        field.asc()
    }

    fun getOffset(pagination: PaginationRequest?): Int =
        (pagination?.pageSize ?: 0) * (pagination?.pageOneBased ?: 1).minus(1)
}
