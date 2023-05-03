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
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
@Schema(description = "Available and used resources of a connector.")
final public class FileStorageUploadRequest {
    @NotBlank
    @Schema(description = "Original name of the uploaded file.", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileName;

    @NotNull
    @Schema(description = "Binary content of the uploaded file.", requiredMode = Schema.RequiredMode.REQUIRED)
    byte[] data;
}
