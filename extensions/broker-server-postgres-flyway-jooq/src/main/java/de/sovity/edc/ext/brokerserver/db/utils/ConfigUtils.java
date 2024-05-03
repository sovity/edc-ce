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
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.ext.brokerserver.db.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.eclipse.edc.spi.system.configuration.Config;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigUtils {

    public static String getRequiredStringProperty(Config config, String name) {
        String value = config.getString(name, "");
        Validate.notBlank(value, "EDC Property '%s' is required".formatted(name));
        return value;
    }
}
