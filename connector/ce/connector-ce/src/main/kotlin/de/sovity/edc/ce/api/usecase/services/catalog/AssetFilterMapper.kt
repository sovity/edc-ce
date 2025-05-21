/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.catalog

import de.sovity.edc.ce.api.common.model.AssetFilterConstraint
import de.sovity.edc.ce.api.common.model.AssetFilterConstraintOperator
import de.sovity.edc.runtime.simple_di.Service
import org.eclipse.edc.spi.query.Criterion

@Service
class AssetFilterMapper {

    fun buildCriteria(constraints: List<AssetFilterConstraint>?): List<Criterion> =
        constraints?.map { buildCriterion(it) } ?: emptyList()

    private fun buildCriterion(constraint: AssetFilterConstraint): Criterion {
        val path = QuerySpecUtils.encodeAssetPropertyPath(constraint.assetPropertyPath)
        return when (constraint.operator ?: error("Operator must not be null")) {
            AssetFilterConstraintOperator.EQ -> {
                Criterion(path, "=", constraint.value)
            }
            AssetFilterConstraintOperator.LIKE -> {
                Criterion(path, "like", constraint.value)
            }
            AssetFilterConstraintOperator.IN -> {
                require(constraint.valueList.isNotEmpty()) {
                    "IN operator requires a non-empty value list"
                }
                Criterion(path, "in", constraint.valueList)
            }
        }
    }

    fun buildAssetFilterConstraints(criteria: List<Criterion>?): List<AssetFilterConstraint> =
        criteria?.map { buildAssetFilterConstraint(it) } ?: emptyList()

    private fun buildAssetFilterConstraint(criterion: Criterion): AssetFilterConstraint {
        val path = QuerySpecUtils.decodeAssetPropertyPath(criterion.operandLeft.toString())
        return when (criterion.operator) {
            "=" -> AssetFilterConstraint(
                path,
                AssetFilterConstraintOperator.EQ,
                getString(criterion.operandRight),
                null
            )

            "like" -> AssetFilterConstraint(
                path,
                AssetFilterConstraintOperator.LIKE,
                getString(criterion.operandRight),
                null
            )

            "in" -> AssetFilterConstraint(
                path,
                AssetFilterConstraintOperator.IN,
                null,
                getStringList(criterion.operandRight),
            )
            // Fallback just to prevent crashes as we cannot control bad data from being written via the Management API
            else -> AssetFilterConstraint(
                path,
                AssetFilterConstraintOperator.EQ,
                getString(criterion.operandRight),
                null,
            )
        }
    }

    private fun getString(value: Any?): String {
        if (value == null) {
            return ""
        }

        return when (value) {
            is String -> value
            is Number -> value.toString()
            is Collection<*> -> value.firstOrNull()?.toString() ?: ""
            // Fallback just to prevent crashes as we cannot control bad data from being written via the Management API
            else -> value.toString()
        }
    }

    private fun getStringList(value: Any?): List<String> {
        if (value == null) {
            return listOf()
        }

        return when (value) {
            is String -> listOf(value)
            is Number -> listOf(value.toString())
            is Collection<*> -> value.map { it.toString() }
            // Fallback just to prevent crashes as we cannot control bad data from being written via the Management API
            else -> listOf(value.toString())
        }
    }
}
