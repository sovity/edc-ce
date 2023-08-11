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
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e.env;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class EnvUtil {

    private EnvUtil() {
    }

    public static String loadRequiredVariable(String variableName) {
        return Optional.ofNullable(System.getenv(variableName))
                .orElseThrow(() -> {
                    var message = String.format(
                            "Missing required environment variable: %s",
                            variableName);
                    return new IllegalArgumentException(message);
                });
    }

    public static URI getUriFromEnv(String variableName) {
        try {
            return new URI(loadRequiredVariable(variableName));
        } catch (URISyntaxException e) {
            var message = String.format("Could not load variable %s", variableName);
            throw new RuntimeException(message, e);
        }
    }
}
