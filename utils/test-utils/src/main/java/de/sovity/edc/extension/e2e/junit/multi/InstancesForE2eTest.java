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

package de.sovity.edc.extension.e2e.junit.multi;

import de.sovity.edc.extension.utils.Lazy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class InstancesForE2eTest {
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
            .anyMatch(clazz::isInstance);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {
        return instances.entrySet()
            .stream()
            .filter(clazz::isInstance)
            .findFirst()
            .map(entry -> (T) entry.getValue().get())
            .orElseThrow(() -> new IllegalArgumentException("No object of type %s".formatted(clazz)));
    }

    public boolean isLazyInitialized(Class<?> clazz) {
        var lazy = instances.entrySet()
            .stream()
            .filter(clazz::isInstance)
            .findFirst()
            .map(Map.Entry::getValue)
            .filter(it -> it.lazy() != null)
            .map(LazyOrValue::lazy)
            .orElseThrow(() -> new IllegalArgumentException("No lazy object of type %s".formatted(clazz)));
        return lazy.isInitialized();
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
