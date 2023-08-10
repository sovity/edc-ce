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
import de.sovity.edc.extension.e2e.connector.config.part.DatasourceConfigPart;
import de.sovity.edc.extension.e2e.connector.config.part.EdcApiGroupConfigPart;
import de.sovity.edc.extension.e2e.connector.config.part.EdcConfigPart;
import de.sovity.edc.extension.e2e.connector.config.part.SimpleConfigPart;
import lombok.Builder;
import lombok.Singular;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Builder
public record EdcConfig(
        @Singular List<DatasourceConfigPart> datasourceConfigParts,
        @Singular List<SimpleConfigPart> simpleConfigParts,
        Map<EdcApiGroup, EdcApiGroupConfigPart> apiGroupConfigMap
) {

    public Map<String, String> getConfigAsMap() {
        var configStream = Stream.of(
                apiGroupConfigMap.values().stream().toList(),
                datasourceConfigParts,
                simpleConfigParts);
        return new HashMap<>() {
            {
                configStream.flatMap(List::stream)
                        .map(EdcConfigPart::toEdcSettingMap)
                        .forEach(this::putAll);
            }
        };
    }

    public EdcApiGroupConfigPart getApiGroupConfig(EdcApiGroup apiGroup) {
        return apiGroupConfigMap.get(apiGroup);
    }

    public static class EdcConfigBuilder {
        public EdcConfigBuilder configProperty(String key, String value) {
            this.simpleConfigPart(new SimpleConfigPart(key, value));
            return this;
        }
    }
}
