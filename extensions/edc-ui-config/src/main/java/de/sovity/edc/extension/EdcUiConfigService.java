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

import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

public class EdcUiConfigService {

    private final ServiceExtensionContext context;

    public EdcUiConfigService(ServiceExtensionContext context) {
        this.context = context;
    }

    public Map<String, String> getEdcUiProperties() {
        var props = new HashMap<>(this.getMappedContextProperties());
        props.put(EdcUiConfigProperty.CONNECTOR_ENDPOINT.getEdcUiPropertyName(), this.buildConnectorEndpoint());
        props.putAll(this.mapKeys(this.getEdcUiContextProperties(), this::toFullCapsSnakeCase));
        return props;
    }

    private Map<String, String> getMappedContextProperties() {
        return stream(EdcUiConfigProperty.values())
                .filter(it -> it.getContextPropertyOrNull() != null)
                .collect(toMap(
                        EdcUiConfigProperty::getEdcUiPropertyName,
                        property -> this.getMappedContextPropertyValue(
                                property.getEdcUiPropertyName(),
                                property.getContextPropertyOrNull()
                        )
                ));
    }

    private String getMappedContextPropertyValue(String edcUiPropertyName, String contextPropertyName) {
        return context.getConfig().getString(
                contextPropertyName,
                String.format(
                        "Unset %s in EDC backend is required for EDC UI property %s.",
                        contextPropertyName,
                        edcUiPropertyName
                )
        );
    }

    private Map<String, String> getEdcUiContextProperties() {
        return context.getConfig().getRelativeEntries("edc.ui.");
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
        return context.getConfig().getString(
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
