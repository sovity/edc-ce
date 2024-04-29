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

package de.sovity.edc.ext.brokerserver.dao.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Collection;

/**
 * PostgreSQL + JooQ Utils
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostgresqlUtils {

    /**
     * Replaces the IN operation with "field = ANY(...)"
     *
     * @param field  field
     * @param values values
     * @return condition
     */
    public static Condition in(Field<String> field, Collection<String> values) {
        return field.eq(DSL.any(values.toArray(String[]::new)));
    }
}
