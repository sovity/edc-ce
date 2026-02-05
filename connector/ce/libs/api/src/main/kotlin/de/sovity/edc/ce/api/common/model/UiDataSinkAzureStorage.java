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
package de.sovity.edc.ce.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "AZURE_STORAGE type Data Sink.")
public class UiDataSinkAzureStorage {
    @Schema(
        description = "Storage Account Name. Note, that the corresponding secret must be stored in the vault under storageAccountName + '-key1'",
        example = "storage-account",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String storageAccountName;

    @Schema(
        description = "Container Name of the Blob Storage",
        example = "container",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String containerName;

    @Schema(
        description = "Folder Name of the Blob Storage",
        example = "folder",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Nullable
    private String folderName;

    @Schema(
        description = "Blob Name of the Blob Storage",
        example = "folder",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Nullable
    private String blobName;
}
