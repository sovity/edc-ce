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

package de.sovity.edc.extension.e2e.connector.config;

import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup;
import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroupConfig;
import lombok.Builder;
import lombok.Singular;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Builder
public class EdcConfigGeneratorImpl implements EdcConfigGenerator {

    @Singular
    private final List<DatasourceConfig> datasourceConfigs;

    @Singular
    private final List<SimpleConfig> simpleConfigs;
    private final Map<EdcApiGroup, EdcApiGroupConfig> apiGroupConfigMap;

    @Override
    public Map<String, String> getConfig() {
        var configStream = Stream.of(
                apiGroupConfigMap.values().stream().toList(),
                datasourceConfigs,
                simpleConfigs);
        return new HashMap<>() {
            {
                configStream.flatMap(List::stream)
                        .map(EdcConfig::toEdcSettingMap)
                        .forEach(this::putAll);
            }
        };
    }

    public static class EdcConfigGeneratorImplBuilder {
        public EdcConfigGeneratorImplBuilder configProperty(String key, String value) {
            this.simpleConfig(new SimpleConfig(key, value));
            return this;
        }
    }
}
