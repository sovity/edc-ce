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

package de.sovity.edc.ext.brokerserver.services.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EdcConfigPropertyUtils {
    /**
     * For better refactoring it is better if the string constant is
     * found in the code as it is used and documented.
     *
     * @param envVarName e.g. "BROKER_SERVER_SOME_CONFIG_SETTING"
     * @return e.g. "broker.server.some.config.setting"
     */
    public static String toEdcProp(String envVarName) {
        return Arrays.stream(envVarName.split("_"))
                .map(String::toLowerCase)
                .collect(joining("."));
    }

}
