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

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class InstancesForJunitTest implements ParameterResolver {
    private final Map<Class<?>, LazyOrValue> instances = new HashMap<>();
    private final List<ParameterResolver> childResolvers = new ArrayList<>();

    public void addParameterResolver(ParameterResolver resolver) {
        childResolvers.add(resolver);
    }

    public void put(Object object) {
        instances.put(object.getClass(), LazyOrValue.ofValue(object));
    }

    public <T> void putLazy(Class<T> clazz, Supplier<T> factory) {
        instances.put(clazz, LazyOrValue.ofLazy(new Lazy<>(factory)));
    }

    public boolean has(Class<?> clazz) {
        return supportsParameter(dummyParameterContext(clazz), null);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return (T) resolveParameter(dummyParameterContext(clazz), null);
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

        return instances.entrySet().stream().anyMatch(isSubclassOfEntry(clazz)) ||
            childResolvers.stream().anyMatch(r -> r.supportsParameter(parameterContext, extensionContext));
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        val clazz = parameterContext.getParameter().getType();
        return instances.entrySet()
            .stream()
            .filter(isSubclassOfEntry(clazz))
            .findFirst()
            .map(entry -> entry.getValue().get())
            .orElseGet(() -> childResolvers.stream()
                .filter(r -> r.supportsParameter(parameterContext, extensionContext))
                .findFirst()
                .orElseThrow(() -> new ParameterResolutionException(
                    "No resolver found for type %s in list of resolvers and instances".formatted(clazz))
                )
                .resolveParameter(parameterContext, extensionContext));
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ParameterContext dummyParameterContext(Class<?> type) {
        // While the ParameterContext API supports resolving all kinds of
        // templated Array types etc, we only care about supporting resolving
        // classes by java.lang.class
        var parameter = mock(Parameter.class);
        when(parameter.getType()).thenReturn((Class) type);
        when(parameter.getParameterizedType()).thenReturn(type);

        var parameterContext = mock(ParameterContext.class);
        when(parameterContext.getParameter()).thenReturn(parameter);
        return parameterContext;
    }
}
