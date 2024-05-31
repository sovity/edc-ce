/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.extension;

import org.eclipse.edc.spi.system.configuration.Config;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EdcUiConfigService {

    private final Config config;

    public EdcUiConfigService(Config config) {
        this.config = config;
    }

    public Map<String, String> getEdcUiProperties() {
        return mapKeys(config.getRelativeEntries("edc.ui."), this::toFullCapsSnakeCase);
    }

    private String toFullCapsSnakeCase(String edcPropertyName) {
        return edcPropertyName.replace(".", "_").toUpperCase();
    }

    private <K, V, M> Map<M, V> mapKeys(Map<K, V> map, Function<K, M> mapper) {
        return map.keySet().stream().collect(Collectors.toMap(mapper, map::get));
    }
}
