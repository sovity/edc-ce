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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.net.URISyntaxException;

import static java.util.Objects.requireNonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvUtil {

    public static String getEnvVar(String variableName) {
        return requireNonNull(System.getenv(variableName), () ->
                "Missing required environment variable: %s".formatted(variableName));
    }

    public static URI getEnvVarUri(String variableName) {
        try {
            return new URI(getEnvVar(variableName));
        } catch (URISyntaxException e) {
            var message = "Failed reading environment variable as URI: %s".formatted(variableName);
            throw new IllegalArgumentException(message, e);
        }
    }

}
