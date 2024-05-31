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

import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;

public class ParameterizationCompatibilityUtils {
    private static final String WORKAROUND = "https://sovity.de/workaround/proxy/param/";

    private static final Map<String, String> MAPPINGS = Map.of(

            EDC_NAMESPACE + "method", WORKAROUND + "method",
            EDC_NAMESPACE + "pathSegments", WORKAROUND + "pathSegments",
            EDC_NAMESPACE + "queryParams", WORKAROUND + "queryParams",
            EDC_NAMESPACE + "body", WORKAROUND + "body",
            EDC_NAMESPACE + "mediaType", WORKAROUND + "mediaType",
            EDC_NAMESPACE + "contentType", WORKAROUND + "mediaType"
    );

    public DataAddress enrich(DataAddress dataAddress, Map<String, String> transferProcessProperties) {
        if(transferProcessProperties == null) {
            return dataAddress;
        }


        HashMap<String, Object> combined = new HashMap<>(dataAddress.getProperties());

        for (val property : transferProcessProperties.entrySet()) {
            val workaroundProperty = MAPPINGS.get(property.getKey());
            if (workaroundProperty != null) {
                combined.put(workaroundProperty, property.getValue());
            }
        }

        return DataAddress.Builder.newInstance()
                .properties(combined)
                .build();
    }
}
