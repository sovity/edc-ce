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
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Schema(description = "Available and used resources of a connector.")
public class FileStorage {
    @Schema(description = "Identifier of the file storage object", requiredMode = Schema.RequiredMode.REQUIRED)
    String id;

    @Schema(description = "Original name of the file.", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileName;

    //ToDo: Not sure if this should be detected based on the extension or based on an enum which is selected by
    //  the uploader via the UI
    @Schema(description = "Content which is presented in the file.")
    String contentType;

    //ToDo: wondering if this is required. In the end the amount of bytes can be easily calculated.
    // It might also impact the formatting in the UI.
    @Schema(description = "Size of the file expressed in a readable format of amount of MB's.", requiredMode = Schema.RequiredMode.REQUIRED)
    String sizeReadable;

    //ToDo: should the max length be defined? It was mentioned that the file should not be larger then 10MB
    @Schema(description = "Size of the file expressed in bytes.", requiredMode = Schema.RequiredMode.REQUIRED)
    Long size;

    @Schema(description = "Map containing the asset properties of the storage object.")
    Map<String, String> assetProperties;

    @Schema(description = "Creation date of the FileStore object.", requiredMode = Schema.RequiredMode.REQUIRED)
    Date creationDate;

    @Schema(description = "Date of the last modification of the FileStore object.", requiredMode = Schema.RequiredMode.REQUIRED)
    Date lastModifiedDate;
}
