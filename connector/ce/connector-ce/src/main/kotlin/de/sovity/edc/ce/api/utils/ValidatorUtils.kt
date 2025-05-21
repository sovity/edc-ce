/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils

import jakarta.validation.Validation
import jakarta.validation.ValidatorFactory
import jakarta.ws.rs.BadRequestException

object ValidatorUtils {
    private val FACTORY: ValidatorFactory = Validation.buildDefaultValidatorFactory()

    @JvmStatic
    fun validate(any: Any) {
        val validator = FACTORY.validator
        val constraintViolations = validator.validate(any)
        if (constraintViolations.isNotEmpty()) {
            throw BadRequestException(constraintViolations.joinToString(separator = "\n"))
        }
    }
}
