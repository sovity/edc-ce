/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils;

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
     * @param o object
     * @param fieldNamePath field names
     * @param <T> return type
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
     * @param o object
     * @param fieldName field name
     * @param <T> return type
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
