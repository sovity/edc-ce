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

package de.sovity.edc.ext.wrapper.api.ee.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "Available and used resources of a connector.")
final public class FileStorageAssetCreateRequest {

    @NotBlank
    @Schema(description = "Identifier of the file storage object", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileStorageId;

    @NotEmpty
    @Schema(description = "Map containing the asset properties of the storage object.", requiredMode = Schema.RequiredMode.REQUIRED)
    Map<String, String> assetProperties;
}
