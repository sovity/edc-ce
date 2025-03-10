/*
 * Copyright 2025 sovity GmbH
 * Copyright 2023 Fraunhofer-Institut für Software- und Systemtechnik ISST
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 *     Fraunhofer ISST - contributions to the Eclipse EDC 0.2.0 migration
 */
package de.sovity.edc.ce.api.utils;

import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MapUtils {

    public static <K, T, R> Map<K, R> mapValues(@NonNull Map<K, T> map, @NonNull Function<T, R> valueMapper) {
        return map.entrySet().stream().collect(toMap(Map.Entry::getKey, e -> valueMapper.apply(e.getValue())));
    }

    public static <K, T> Map<K, T> associateBy(Collection<T> collection, Function<T, K> keyExtractor) {
        return collection.stream().collect(Collectors.toMap(keyExtractor, Function.identity(), (a, b) -> {
            throw new IllegalStateException("Duplicate key %s.".formatted(keyExtractor.apply(a)));
        }));
    }

    public static <K, T> Map<T, K> reverse(@NonNull Map<K, T> map) {
        return map.entrySet().stream().collect(toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
}
