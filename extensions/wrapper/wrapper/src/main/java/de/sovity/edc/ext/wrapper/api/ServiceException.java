/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api;

import jakarta.ws.rs.WebApplicationException;
import org.eclipse.edc.service.spi.result.ServiceResult;
import org.eclipse.edc.spi.result.Failure;

import java.util.function.Function;

/**
 * Exception for handling {@link ServiceResult} {@link Failure}s.
 *
 * @see ServiceResult#orElseThrow(Function)
 */
public class ServiceException extends WebApplicationException {
    public ServiceException(Failure failure) {
        super(failure.getFailureDetail(), 500);
    }
}
