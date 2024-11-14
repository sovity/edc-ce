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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.utils.config.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

/**
 * We want to extend {@link org.eclipse.edc.boot.system.runtime.BaseRuntime}, but unfortunately some vital fields are private.
 * <p>
 * Instead of maintaining a little bit more code, we do this. Where there's a will, there's a way.
 */
@UtilityClass
public class ReflectionUtils {

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static <T, R> R getFieldValue(Class<T> clazz, T obj, String fieldName) {
        var field = getField(clazz, fieldName);
        return (R) field.get(obj);
    }

    @SneakyThrows
    public static <T> void setFieldValue(Class<T> clazz, T obj, String fieldName, Object value) {
        var field = getField(clazz, fieldName);
        field.set(obj, value);
    }

    @SneakyThrows
    private static Field getField(Class<?> clazz, String fieldName) {
        var field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }
}
