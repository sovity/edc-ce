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

@Data
@Schema(description = "Available and used resources of a connector.")
final public class FileStorageUploadResult {
    @Schema(description = "Identifier of the file storage object", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileStorageId;

    //ToDo: wondering if this is required. In the end the amount of bytes can be easily calculated.
    // It might also impact the formatting in the UI.
    @Schema(description = "Size of the file expressed in a readable format of amount of MB's.", requiredMode = Schema.RequiredMode.REQUIRED)
    String fileSizeReadable;
}
