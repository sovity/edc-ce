/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.e2e.junit.utils;

import de.sovity.edc.extension.utils.Lazy;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class InstancesForJunitTest implements ParameterResolver {
    private final Map<Class<?>, LazyOrValue> instances = new HashMap<>();

    public void put(Object object) {
        instances.put(object.getClass(), LazyOrValue.ofValue(object));
    }

    public <T> void putLazy(Class<T> clazz, Supplier<T> factory) {
        instances.put(clazz, LazyOrValue.ofLazy(new Lazy<>(factory)));
    }

    public boolean has(Class<?> clazz) {
        return instances.entrySet()
            .stream()
            .anyMatch(isSubclassOfEntry(clazz));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return instances.entrySet()
            .stream()
            .filter(isSubclassOfEntry(clazz))
            .findFirst()
            .map(entry -> (T) entry.getValue().get())
            .orElseThrow(() -> new IllegalArgumentException("No object of type %s".formatted(clazz)));
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> all(Class<T> clazz) {
        return instances.entrySet()
            .stream()
            .filter(isSubclassOfEntry(clazz))
            .map(entry -> (T) entry.getValue().get())
            .toList();
    }

    public boolean isLazyInitialized(Class<?> clazz) {
        var lazy = instances.entrySet()
            .stream()
            .filter(isSubclassOfEntry(clazz))
            .findFirst()
            .map(Map.Entry::getValue)
            .filter(it -> it.lazy() != null)
            .map(LazyOrValue::lazy)
            .orElseThrow(() -> new IllegalArgumentException("No lazy object of type %s".formatted(clazz)));
        return lazy.isInitialized();
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        val clazz = parameterContext.getParameter().getType();
        return has(clazz);
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        val clazz = parameterContext.getParameter().getType();
        if (!has(clazz)) {
            throw new IllegalArgumentException("No object of type %s".formatted(clazz));
        }
        return get(clazz);
    }

    @NotNull
    private Predicate<Map.Entry<Class<?>, LazyOrValue>> isSubclassOfEntry(Class<?> clazz) {
        return it -> clazz.isAssignableFrom(it.getKey());
    }


    private record LazyOrValue(Object value, Lazy<?> lazy) {
        public static LazyOrValue ofValue(Object value) {
            return new LazyOrValue(value, null);
        }

        public static LazyOrValue ofLazy(Lazy<?> lazy) {
            return new LazyOrValue(null, lazy);
        }

        public Object get() {
            if (value != null) {
                return value;
            }
            return lazy.get();
        }
    }
}
