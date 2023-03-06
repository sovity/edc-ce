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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

public class EdcUiConfigService {

    private final Config config;

    public EdcUiConfigService(Config config) {
        this.config = config;
    }

    public Map<String, String> getEdcUiProperties() {
        var props = new HashMap<>(this.mapBackendProperties());
        props.put(EdcUiConfigProperty.CONNECTOR_ENDPOINT.getUiName(), this.buildConnectorEndpoint());
        props.putAll(this.mapPrefixedProperties("edc.ui."));
        return props;
    }

    private Map<String, String> mapBackendProperties() {
        return stream(EdcUiConfigProperty.values())
                .filter(it -> it.getBackendNameOrNull() != null)
                .collect(toMap(
                        EdcUiConfigProperty::getUiName,
                        property -> this.getMappedPropertyValue(
                                property.getUiName(),
                                property.getBackendNameOrNull()
                        )
                ));
    }

    private String getMappedPropertyValue(String uiName, String backendName) {
        return config.getString(
                backendName,
                String.format(
                        "Unset %s in EDC backend is required for EDC UI property %s.",
                        backendName,
                        uiName
                )
        );
    }

    private Map<String, String> mapPrefixedProperties(String prefix) {
        return this.mapKeys(config.getRelativeEntries(prefix), this::toFullCapsSnakeCase);
    }

    private String toFullCapsSnakeCase(String edcPropertyName) {
        return edcPropertyName.replace(".", "_").toUpperCase();
    }

    private <K, V, M> Map<M, V> mapKeys(Map<K, V> map, Function<K, M> mapper) {
        return map.keySet().stream().collect(Collectors.toMap(mapper, map::get));
    }

    private String buildConnectorEndpoint() {
        return urlJoin(getIdsEndpoint(), "data");
    }

    private String getIdsEndpoint() {
        return config.getString(
                "edc.ids.endpoint",
                "http://property.edc-ids-endpoint.not.set.in.edc.backend/ids"
        );
    }

    private String urlJoin(String url, String path) {
        requireNonNull(url, "url");
        requireNonNull(path, "path");

        if (url.endsWith("/")) {
            return url + path;
        }

        return url + "/" + path;
    }
}
