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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Asset Request for Asset Page")
public class AssetRequest {

    @Schema(description = "Data Address", requiredMode = Schema.RequiredMode.REQUIRED)
    private DataAddressDto dataAddressDto;

    @Schema(description = "Properties of the Data Address", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> properties;

    @Schema(description = "Private Properties of the Data Address", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> privateProperties;
}
