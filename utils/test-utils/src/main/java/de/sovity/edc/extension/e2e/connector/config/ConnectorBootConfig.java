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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.extension.e2e.connector.config;

import de.sovity.edc.utils.config.model.ConfigProp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.HashMap;
import java.util.Map;


/**
 * Config before EDC has been started.
 * <p>
 * This does not include all config properties, as the EDC will do defaulting on startup.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class ConnectorBootConfig {
    @Singular("property")
    private Map<ConfigProp, String> properties;

    @Singular("property")
    private Map<String, String> additionalRawProperties;

    public Map<String, String> asMap() {
        var merged = new HashMap<String, String>();
        properties.forEach((prop, value) -> merged.put(prop.getProperty(), value));
        merged.putAll(additionalRawProperties);
        return merged;
    }
}
