/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

package de.sovity.edc.ce.utils

import de.sovity.edc.ce.modules.messaging.contract_termination.tractus_bridge.QuerySpecToJooqMapping
import de.sovity.edc.ce.utils.CriterionOperatorEnum.CONTAINS
import de.sovity.edc.ce.utils.CriterionOperatorEnum.EQUAL
import de.sovity.edc.ce.utils.CriterionOperatorEnum.ILIKE
import de.sovity.edc.ce.utils.CriterionOperatorEnum.IN
import de.sovity.edc.ce.utils.CriterionOperatorEnum.LESS_THAN
import de.sovity.edc.ce.utils.CriterionOperatorEnum.LIKE
import de.sovity.edc.ce.utils.CriterionOperatorEnum.NOT_EQUAL
import de.sovity.edc.ce.utils.dsl.QuerySpecDsl
import org.eclipse.edc.spi.query.QuerySpec
import org.eclipse.edc.spi.query.SortOrder
import org.jooq.Condition
import org.jooq.Field
import org.jooq.Record
import org.jooq.SelectConditionStep
import org.jooq.SelectForUpdateStep
import org.jooq.SelectLimitStep
import org.jooq.SelectWhereStep
import org.jooq.SortField
import org.jooq.impl.DSL

object QuerySpecJooqUtils {
    @QuerySpecDsl
    fun <R : Record> SelectWhereStep<R>.where(
        spec: QuerySpec,
        supportedFilters: List<QuerySpecToJooqMapping>,
    ): SelectConditionStep<R> {
        return if (spec.filterExpression.isNotEmpty()) {
            this.where(applyFilters(spec, supportedFilters))
        } else {
            this.where()
        }
    }

    fun applyFilters(
        spec: QuerySpec,
        supportedFilters: List<QuerySpecToJooqMapping>
    ): Condition = DSL.and(
        spec.filterExpression.map { expression ->
            supportedFilters.first { it.fieldName == expression.operandLeft }.toCondition(expression)
        }
    )

    @QuerySpecDsl
    fun <R : Record> SelectConditionStep<R>.orderBy(
        spec: QuerySpec,
        supportedFields: List<QuerySpecToJooqMapping>
    ): SelectLimitStep<R> {
        val fieldName = spec.sortField ?: return this
        // based on org.eclipse.edc.sql.translation.SqlQueryStatement.parseSortField
        val order = spec.sortOrder ?: SortOrder.DESC
        val sortField = supportedFields.first { it.fieldName == fieldName }.toSortField(order)
        return this.orderBy(sortField)
    }

    @QuerySpecDsl
    fun <R : Record> SelectLimitStep<R>.limitAndOffset(spec: QuerySpec): SelectForUpdateStep<R> =
        this.limit(spec.limit).offset(spec.offset)

    fun <T> buildCondition(field: Field<T>, operatorSymbol: String, value: T): Condition {
        val operator = try {
            CriterionOperatorEnum.parseFromSymbol(operatorSymbol)
        } catch (e: NoSuchElementException) {
            error("Unsupported operator ${operatorSymbol}: ${e.message}")
        }

        return when (operator) {
            EQUAL -> field.eq(value)
            NOT_EQUAL -> field.notEqual(value)
            IN -> field.`in`(value)
            LIKE -> field.like(value.toString())
            ILIKE -> field.likeIgnoreCase(value.toString())
            CONTAINS -> field.contains(value)
            LESS_THAN -> field.lessThan(value)
        }
    }

    fun <T> Field<T>.direction(order: SortOrder): SortField<T> =
        when (order) {
            SortOrder.ASC -> this.asc()
            SortOrder.DESC -> this.desc()
        }

    fun <T> buildSortField(field: Field<T>, order: SortOrder): SortField<T> =
        when (order) {
            SortOrder.ASC -> field.asc()
            SortOrder.DESC -> field.desc()
        }

}
