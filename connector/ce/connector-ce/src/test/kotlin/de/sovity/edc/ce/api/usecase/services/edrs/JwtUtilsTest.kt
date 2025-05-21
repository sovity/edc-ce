/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.edrs

import de.sovity.edc.ce.modules.dataspaces.sovity.edrs.JwtUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class JwtUtilsTest {
    private val jwtUtils = JwtUtils()

    @Test
    fun `test getExpirationDate with proper expiration date`() {
        // arrange
        // created with http://jwtbuilder.jamiekurtz.com/
        val token = """
            eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSl
            dUIEJ1aWxkZXIiLCJpYXQiOjE3NDQ4MDgzNDYsImV4cCI6MTc0NDgwOTU1N
            CwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFt
            cGxlLmNvbSIsIkdpdmVuTmFtZSI6IkpvaG5ueSIsIlN1cm5hbWUiOiJSb2N
            rZXQiLCJFbWFpbCI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk
            1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXX0.DxdhGSGclQCKz
            2loHStc3Ei9q6p2Ht2FCEp165z7azk
        """.lines().joinToString("") { it.trim() }

        // act
        val expirationDate = jwtUtils.getExpirationDateOrNull(token)

        // assert
        assertThat(expirationDate).isAtSameInstantAs(
            OffsetDateTime.parse("2025-04-16T13:19:14Z")
        )
    }

    @Test
    fun `test getExpirationDate`() {
        // arrange
        // created with http://jwtbuilder.jamiekurtz.com/
        val token = """
            eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSl
            dUIEJ1aWxkZXIiLCJpYXQiOjE3NDQ4MDgzNDYsImV4cCI6MTc0NDgwOTU1N
            CwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJvY2tldEBleGFt
            cGxlLmNvbSIsIkdpdmVuTmFtZSI6IkpvaG5ueSIsIlN1cm5hbWUiOiJSb2N
            rZXQiLCJFbWFpbCI6Impyb2NrZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk
            1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXX0.DxdhGSGclQCKz
            2loHStc3Ei9q6p2Ht2FCEp165z7azk
        """.lines().joinToString("") { it.trim() }

        // act
        val expirationDate = jwtUtils.getExpirationDateOrNull(token)

        // assert
        assertThat(expirationDate).isAtSameInstantAs(
            OffsetDateTime.parse("2025-04-16T13:19:14Z")
        )
    }
}
