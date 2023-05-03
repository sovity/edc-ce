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
import lombok.Data;

@Data
@Schema(description = "Available and used resources of a connector.")
public final class FileStorageUploadResult {
    @NotBlank
    @Schema(description = "Identifier of the file storage object.", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileStorageId;

    @NotBlank
    @Schema(description = "File name which contains the value for the KEY_ASSET_FILE_NAME asset property value.", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileName;

    @NotBlank
    @Schema(description = "File extension which contains the value for the KEY_ASSET_FILE_EXTENSION asset property value.", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileExtension;

    @NotBlank
    @Schema(description = "Size of the file in bytes which contains the value for the KEY_ASSET_BYTE_SIZE asset property value", requiredMode = Schema.RequiredMode.REQUIRED)
    String byteSize;
}
