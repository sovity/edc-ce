/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge

import org.eclipse.edc.spi.query.Criterion
import org.eclipse.edc.spi.query.SortOrder
import org.jooq.Condition
import org.jooq.SortField

class QuerySpecToJooqMapping(
    val fieldName: String,
    val toCondition: (expression: Criterion) -> Condition,
    val toSortField: (order: SortOrder) -> SortField<*>
)
