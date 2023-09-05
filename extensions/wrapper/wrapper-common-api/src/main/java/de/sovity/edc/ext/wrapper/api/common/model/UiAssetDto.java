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

package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "Asset Details")
public class UiAssetDto {
    @Schema(description = "Asset JSON-LD", requiredMode = Schema.RequiredMode.REQUIRED)
    private String assetJsonLd;

    @Schema(description = "a Map to capture any Additional properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> additionalProperties;

    @Schema(description = "a Map to capture any Additional JSON properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> additionalJsonProperties;
}
