/*
 * Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.utils;

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
