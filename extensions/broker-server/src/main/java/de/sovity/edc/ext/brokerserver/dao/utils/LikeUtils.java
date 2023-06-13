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
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

/**
 * Utilities for dealing with PostgreSQL Like Operation values
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeUtils {

    /**
     * Create LIKE condition value for "field contains word".
     *
     * @param field field
     * @param word  word
     * @return "%escapedWord%"
     */
    public static Condition contains(Field<String> field, String word) {
        if (StringUtils.isBlank(word)) {
            return DSL.trueCondition();
        }

        return field.like("%" + escape(word) + "%");
    }


    /**
     * Escapes "\", "%", "_" in given string for a LIKE operation
     *
     * @param string unescaped string
     * @return escaped string
     */
    public static String escape(String string) {
        return string.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
