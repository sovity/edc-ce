/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.catalog

import de.sovity.edc.utils.jsonld.vocab.Prop
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class QuerySpecUtilsTest {
    @Test
    fun `encode path`() {
        // arrange
        val path = listOf(Prop.SovityDcatExt.DISTRIBUTION, Prop.Dcat.MEDIATYPE)

        // act
        val actual = QuerySpecUtils.encodeAssetPropertyPath(path)

        // assert
        assertThat(actual).isEqualTo(
            "'properties'.'https://semantic.sovity.io/dcat-ext#distribution'.'http://www.w3.org/ns/dcat#mediaType'",
        )
    }

    @Test
    fun `encode empty path`() {
        // act & assert
        assertThatThrownBy {
            QuerySpecUtils.encodeAssetPropertyPath(listOf())
        }.hasMessage("Property path must not be empty")
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `encode usage of single quote`() {
        // act & assert
        assertThatThrownBy {
            QuerySpecUtils.encodeAssetPropertyPath(listOf("'"))
        }.hasMessage("Property of path must not contain single quotes, as it is used as an escape character: [properties, ']")
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `decode well-escaped path`() {
        // arrange
        val path = "'properties'.'https://semantic.sovity.io/dcat-ext#distribution'.'http://www.w3.org/ns/dcat#mediaType'"

        // act
        val actual = QuerySpecUtils.decodeAssetPropertyPath(path)

        // assert
        assertThat(actual).isEqualTo(listOf(Prop.SovityDcatExt.DISTRIBUTION, Prop.Dcat.MEDIATYPE))
    }

    @Test
    fun `decode unescaped path`() {
        // arrange
        val path = "https://semantic.sovity.io/dcat-ext#distribution"

        // act
        val actual = QuerySpecUtils.decodeAssetPropertyPath(path)

        // assert
        assertThat(actual).isEqualTo(listOf(Prop.SovityDcatExt.DISTRIBUTION))
    }
}
