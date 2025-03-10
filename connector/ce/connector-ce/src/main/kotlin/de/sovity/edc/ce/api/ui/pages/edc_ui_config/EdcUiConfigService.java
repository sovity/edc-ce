/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.edc_ui_config;

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
