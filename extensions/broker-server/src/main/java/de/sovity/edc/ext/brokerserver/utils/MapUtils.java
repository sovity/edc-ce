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

package de.sovity.edc.ext.brokerserver.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapUtils {
    public static <K, T> Map<K, T> associateBy(Collection<T> collection, Function<T, K> keyExtractor) {
        return collection.stream().collect(Collectors.toMap(keyExtractor, Function.identity(), (a, b) -> {
            throw new IllegalStateException("Duplicate key %s.".formatted(keyExtractor.apply(a)));
        }));
    }
}
