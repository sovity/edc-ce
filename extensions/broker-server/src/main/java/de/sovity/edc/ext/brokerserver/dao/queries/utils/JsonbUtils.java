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

package de.sovity.edc.ext.brokerserver.dao.queries.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.JSONB;

/**
 * Utilities for dealing with {@link org.jooq.JSONB} fields.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonbUtils {

    /**
     * Returns the data of the given {@link JSONB} or null.
     *
     * @param jsonb {@link org.jooq.JSON}
     * @return data or null
     */
    public static String getDataOrNull(JSONB jsonb) {
        return jsonb == null ? null : jsonb.data();
    }
}
