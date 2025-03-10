/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.edc_ui_config;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.Map;

@Produces({MediaType.APPLICATION_JSON})
@Path("/edc-ui-config")
public class EdcUiConfigController {
    private final EdcUiConfigService edcUiConfigService;

    public EdcUiConfigController(EdcUiConfigService edcUiConfigService) {
        this.edcUiConfigService = edcUiConfigService;
    }

    @GET
    public Map<String, String> getEdcUiProperties() {
        return edcUiConfigService.getEdcUiProperties();
    }
}
