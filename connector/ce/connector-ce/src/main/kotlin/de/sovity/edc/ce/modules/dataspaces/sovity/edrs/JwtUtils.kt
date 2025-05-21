/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.dataspaces.sovity.edrs

import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.edc.runtime.simple_di.Service
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Base64

@Service
class JwtUtils {
    private val objectMapper = ObjectMapper()

    fun getExpirationDateOrNull(token: String): OffsetDateTime? {
        val jwtParts = token.split(".")

        if (jwtParts.size != 3) {
            error("Invalid EDR token")
        }

        val jwtPayload = jwtParts[1]
        val decodedPayload = Base64.getDecoder().decode(jwtPayload)

        val claims = objectMapper.readTree(decodedPayload)
        val expClaim = claims["exp"]

        if (expClaim == null || expClaim.isNull) {
            return null
        }

        val expClaimLong = when {
            expClaim.isLong || expClaim.isInt -> expClaim.asLong()
            expClaim.isTextual -> expClaim.asText().toLongOrNull()
            else -> null
        }

        if (expClaimLong == null || expClaimLong <= 0L) {
            error("Invalid EDR token, invalid expiration claim: $expClaim")
        }

        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(expClaimLong), ZoneOffset.UTC)
    }
}
