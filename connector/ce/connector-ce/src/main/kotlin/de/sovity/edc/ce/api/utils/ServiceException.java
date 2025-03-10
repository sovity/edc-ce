/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils;

import jakarta.ws.rs.WebApplicationException;
import org.eclipse.edc.spi.result.Failure;

public class ServiceException extends WebApplicationException {
    public ServiceException(Failure failure) {
        super(failure.getFailureDetail(), 500);
    }
}
