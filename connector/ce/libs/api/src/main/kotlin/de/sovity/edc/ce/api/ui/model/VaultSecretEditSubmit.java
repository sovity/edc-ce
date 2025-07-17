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
package de.sovity.edc.ce.api.ui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.sovity.edc.ce.api.annotations.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Submit Request for editing a User Managed Vault Secret")
public class VaultSecretEditSubmit {
    @Nullable
    @Schema(
        description = "Secret value of the vault secret. If null, will not change the stored value",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @NullOrNotBlank(message = "Value cannot be blank if set")
    private String value;

    @Schema(description = "Description", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Description cannot be blank")
    private String description;
}
