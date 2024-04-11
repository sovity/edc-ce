/*
 *  Copyright (c) 2024 sovity GmbH
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


package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import lombok.val;
import org.eclipse.edc.spi.types.domain.DataAddress;

import java.util.HashMap;
import java.util.Map;

public class ParameterizationCompatibilityUtils {
    public DataAddress enrich(DataAddress dataAddress, Map<String, String> transferProcessProperties) {
        if(transferProcessProperties == null) {
            return dataAddress;
        }

        Map<String, String> mappings = Map.of(
                "https://w3id.org/edc/v0.0.1/ns/method", "https://sovity.de/workaround/proxy/param/method",
                "https://w3id.org/edc/v0.0.1/ns/pathSegments", "https://sovity.de/workaround/proxy/param/pathSegments",
                "https://w3id.org/edc/v0.0.1/ns/queryParams", "https://sovity.de/workaround/proxy/param/queryParams",
                "https://w3id.org/edc/v0.0.1/ns/body", "https://sovity.de/workaround/proxy/param/body",
                "https://w3id.org/edc/v0.0.1/ns/mediaType", "https://sovity.de/workaround/proxy/param/mediaType",
                "https://w3id.org/edc/v0.0.1/ns/contentType", "https://sovity.de/workaround/proxy/param/mediaType"
        );

        HashMap<String, Object> combined = new HashMap<>(dataAddress.getProperties());

        for (val e : transferProcessProperties.entrySet()) {
            val mapping = mappings.get(e.getKey());
            if (mapping != null) {
                combined.put(mapping, e.getValue());
            }
        }

        return DataAddress.Builder.newInstance()
                .properties(combined)
                .build();
    }
}
