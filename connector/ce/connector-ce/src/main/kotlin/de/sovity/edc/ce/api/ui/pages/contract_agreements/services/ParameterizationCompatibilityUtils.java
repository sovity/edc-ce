/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.contract_agreements.services;

import de.sovity.edc.runtime.simple_di.Service;
import lombok.val;
import org.eclipse.edc.spi.types.domain.DataAddress;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;

@Service
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
        if (transferProcessProperties == null) {
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
