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

package de.sovity.edc.ext.wrapper.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

/**
 * Where there's a will, there's a way.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldAccessUtils {

    /**
     * Access an object's private field's values recursively.
     *
     * @param o             object
     * @param fieldNamePath field names
     * @param <T>           return type
     * @return field value
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T accessField(Object o, List<String> fieldNamePath) {
        Object result = o;
        for (String fieldName : fieldNamePath) {
            result = accessField(result, fieldName);
        }
        return (T) result;
    }

    /**
     * Access an object's private field's value.
     *
     * @param o         object
     * @param fieldName field name
     * @param <T>       return type
     * @return field value
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T accessField(Object o, String fieldName) {
        var field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(o);
    }
}
