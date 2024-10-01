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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.utils.config.utils;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.stream.Stream;

@UtilityClass
public class UrlPathUtils {
    public static String urlPathJoin(String... parts) {
        return Stream.of(parts)
            .filter(Objects::nonNull)
            .filter(it -> !it.isEmpty())
            .reduce("", (cur, add) -> {
                // Special Case: https://
                if (add.contains("://")) {
                    return add;
                }

                // Join with a single slash
                if (cur.endsWith("/") && add.startsWith("/")) {
                    return cur + add.substring(1);
                } else if (!cur.isEmpty() && !cur.endsWith("/") && !add.startsWith("/")) {
                    return cur + "/" + add;
                } else {
                    return cur + add;
                }
            });
    }
}
