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

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class InstancesForEachConnector<S> {
    private final List<S> sides;
    private final Map<S, Map<Class<?>, Object>> instances = sides.stream()
        .collect(toMap(identity(), unused -> new HashMap<>()));


    public <T> List<T> all(Class<T> clazz) {
        return sides.stream().flatMap(side -> filterBySideAndIsInstance(side, clazz)).toList();
    }

    public void put(S side, Object object) {
        instances.get(side).put(object.getClass(), object);
    }

    public boolean has(S side, Class<?> clazz) {
        return filterBySideAndIsInstance(side, clazz).findAny().isPresent();
    }

    public <T> T get(S side, Class<T> clazz) {
        return filterBySideAndIsInstance(side, clazz)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No object of type %s for side %s found".formatted(clazz, side)));
    }

    @NotNull
    private <T> Stream<T> filterBySideAndIsInstance(S side, Class<T> clazz) {
        return instances.get(side).entrySet()
            .stream()
            .filter(clazz::isInstance)
            .map(entry -> clazz.cast(entry.getValue()));
    }
}
