/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Differentiate 'Live' Data Offers that have a real data source from " +
        "'On Request' Data Offers that contain only a contact email address for requesting an individual data offer.",
    enumAsRef = true
)
public enum DataSourceAvailability {
    LIVE,
    ON_REQUEST
}
