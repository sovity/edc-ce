/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.api.ee.model;

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
@Schema(description = "Represents a stored file in the file storage extension")
public class StoredFile {
    @Schema(description = "Identifier of the StoredFile object",
        example = "stored-file-001",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String storedFileId;

    @Schema(description = "The name of file.",
        example = "afilename.csv",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileName;

    @Schema(description = "The extension of the file.",
        example = "csv",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileExtension;

    @Schema(description = "The media type of the file.",
        example = "text/csv",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String mediaType;

    @Schema(description = "Size of the file in bytes.",
        example = "1024",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String byteSize;

    @Schema(description = "Map containing the asset properties of the stored file." +
        "<br> An empty map is set as a response to a file storage request. <br> Only upon a asset creation request " +
        "the asset properties are set.",
        example = "{\"asset:prop:id\": \"some-asset-1\",\n \"asset:prop:originator\": \"http://my-example-connector/api/v1/ids\"}",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, String> assetProperties;

    @Schema(description = "Creation date of the StoredFile object.",
        example = "2023-05-05T12:00:00.000+02:00",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime creationDate;

    @Schema(description = "Date of the last modification of the StoredFile object.",
        example = "2023-05-05T14:00:00.000+02:00",
        requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime lastModifiedDate;
}
