/*
 * Copyright 2025 sovity GmbH
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
 */
package de.sovity.edc.ce.libs.mappers.dataaddress.http;

import de.sovity.edc.runtime.simple_di.Service;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;


@RequiredArgsConstructor
@Service
public class HttpHeaderMapper {
    public Map<String, String> buildHeaderProps(@Nullable Map<String, String> headers) {
        return mapKeys(
            headers,
            key -> {
                if ("content-type".equalsIgnoreCase(key)) {
                    // Content-Type is overridden by a special Data Address property
                    // So we should set that instead of attempting to set a header
                    return Prop.Edc.CONTENT_TYPE;
                } else {
                    return "header:%s".formatted(key);
                }
            }
        );
    }

    private <K, L, T> Map<L, T> mapKeys(
        @Nullable Map<K, T> map,
        @NonNull Function<K, L> keyMapper
    ) {
        if (map == null) {
            return Collections.emptyMap();
        }

        return map.entrySet().stream().collect(toMap(
            e -> keyMapper.apply(e.getKey()),
            Map.Entry::getValue,
            (v1, v2) -> v1 // lenient merge function
        ));
    }
}
