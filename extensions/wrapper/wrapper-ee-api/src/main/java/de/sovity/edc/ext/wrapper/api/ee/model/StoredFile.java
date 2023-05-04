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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File storage resource of a connector.")
public class StoredFile {
    @Schema(description = "Identifier of the file storage object", requiredMode = Schema.RequiredMode.REQUIRED)
    String id;

    @Schema(description = "File name which contains the value for the KEY_ASSET_FILE_NAME asset property value.", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileName;

    @Schema(description = "File extension which contains the value for the KEY_ASSET_FILE_EXTENSION asset property value.", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileExtension;

    @Schema(description = "Size of the file in bytes which contains the value for the KEY_ASSET_BYTE_SIZE asset property value", requiredMode = Schema.RequiredMode.REQUIRED)
    String byteSize;

    @Schema(description = "Map containing the asset properties of the storage object.")
    Map<String, String> assetProperties;

    @Schema(description = "Creation date of the FileStore object.", requiredMode = Schema.RequiredMode.REQUIRED)
    OffsetDateTime creationDate;

    @Schema(description = "Date of the last modification of the FileStore object.", requiredMode = Schema.RequiredMode.REQUIRED)
    OffsetDateTime lastModifiedDate;
}
