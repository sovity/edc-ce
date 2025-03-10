/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.BadRequestException;
import lombok.val;

public class ValidatorUtils {
    private static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();

    public static void validate(Object object) {
        val validator = FACTORY.getValidator();
        val constraintViolations = validator.validate(object);
        if (!constraintViolations.isEmpty()) {
            throw new BadRequestException(new ConstraintViolationException("Failed to validate", constraintViolations));
        }
    }
}
