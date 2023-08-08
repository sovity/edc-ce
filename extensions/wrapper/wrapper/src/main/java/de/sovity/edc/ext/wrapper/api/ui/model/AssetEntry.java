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

package de.sovity.edc.ext.wrapper.api.ui.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Asset Entry for Asset Page")
public class AssetEntry {

    @Schema(description = "Asset Properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> properties;

    @Schema(description = "Asset Private Properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> privateProperties;

    @Schema(description = "Asset Creation Date", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long creationDate;







}
