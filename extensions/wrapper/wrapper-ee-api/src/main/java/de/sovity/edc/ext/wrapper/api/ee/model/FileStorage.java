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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

@Data
@Schema(description = "Available and used resources of a connector.")
public class FileStorage {
    @NotBlank
    @Schema(description = "Identifier of the file storage object", requiredMode = Schema.RequiredMode.REQUIRED)
    String id;

    @NotBlank
    @Schema(description = "Original name of the file.", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileName;

    @NotBlank
    @Schema(description = "Size of the file expressed in bytes.", requiredMode = Schema.RequiredMode.REQUIRED, maximum = "1048576")
    BigInteger byteSize;

    @Schema(description = "Map containing the asset properties of the storage object.")
    Map<String, String> assetProperties;

    @NotNull
    @Schema(description = "Creation date of the FileStore object.", requiredMode = Schema.RequiredMode.REQUIRED)
    Date creationDate;

    @NotNull
    @Schema(description = "Date of the last modification of the FileStore object.", requiredMode = Schema.RequiredMode.REQUIRED)
    Date lastModifiedDate;
}
