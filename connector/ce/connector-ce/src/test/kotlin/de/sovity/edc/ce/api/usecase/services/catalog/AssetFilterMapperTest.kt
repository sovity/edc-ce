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
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.edc.spi.query.Criterion
import org.junit.jupiter.api.Test

class AssetFilterMapperTest {
    private val assetFilterMapper = AssetFilterMapper()

    @Test
    fun `buildCriteria eq`() {
        // arrange
        val constraints = listOf(
            AssetFilterConstraint(
                listOf("id"),
                AssetFilterConstraintOperator.EQ,
                "123",
                null
            )
        )

        // act
        val criteria = assetFilterMapper.buildCriteria(constraints)
        val reversion = assetFilterMapper.buildAssetFilterConstraints(criteria)

        // assert
        assertThat(criteria.single())
            .usingRecursiveComparison()
            .isEqualTo(Criterion("'properties'.'id'", "=", "123"))

        assertThat(reversion)
            .usingRecursiveComparison()
            .isEqualTo(constraints)
    }

    @Test
    fun `buildCriteria like`() {
        // arrange
        val constraints = listOf(
            AssetFilterConstraint(
                listOf("id"),
                AssetFilterConstraintOperator.LIKE,
                "123",
                null
            )
        )

        // act
        val criteria = assetFilterMapper.buildCriteria(constraints)
        val reversion = assetFilterMapper.buildAssetFilterConstraints(criteria)

        // assert
        assertThat(criteria.single())
            .usingRecursiveComparison()
            .isEqualTo(Criterion("'properties'.'id'", "like", "123"))
        assertThat(reversion)
            .usingRecursiveComparison()
            .isEqualTo(constraints)
    }

    @Test
    fun `buildCriteria in`() {
        // arrange
        val constraints = listOf(
            AssetFilterConstraint(
                listOf("id"),
                AssetFilterConstraintOperator.IN,
                null,
                listOf("123", "456")
            )
        )

        // act
        val criteria = assetFilterMapper.buildCriteria(constraints)
        val reversion = assetFilterMapper.buildAssetFilterConstraints(criteria)

        // assert
        assertThat(criteria.single())
            .usingRecursiveComparison()
            .isEqualTo(Criterion("'properties'.'id'", "in", listOf("123", "456")))
        assertThat(reversion)
            .usingRecursiveComparison()
            .isEqualTo(constraints)
    }

    @Test
    fun `irrelevant edge cases that simply shouldn't crash as to not brick our EDCs`() {
        assetFilterMapper.buildCriteria(
            listOf(
                AssetFilterConstraint(
                    listOf("id"),
                    AssetFilterConstraintOperator.EQ,
                    null,
                    null
                ),
            )
        )
        assetFilterMapper.buildAssetFilterConstraints(listOf(
            Criterion("'id'", "=", null),
            Criterion("'id'", "=", listOf<String>()),
            Criterion("'id'", "=", listOf(5)),
            Criterion("'id'", "=", listOf(true)),
            Criterion("'id'", "=", listOf(5.2)),
            Criterion("'id'", "unknown", null),
            Criterion("", "unknown", null),
        ))
    }
}
