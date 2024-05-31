/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.utils.catalog;

import org.eclipse.edc.spi.result.Failure;

public class DspCatalogServiceException extends RuntimeException {
    public DspCatalogServiceException(String message) {
        super(message);
    }

    public static DspCatalogServiceException ofFailure(Failure failure) {
        return new DspCatalogServiceException(failure.getFailureDetail());
    }
}
