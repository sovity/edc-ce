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

package de.sovity.edc.ext.wrapper.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "A value either inlined or to be fetched from the Vault. " +
    "Raw secret values will land in the database and will be retrievable via the " +
    "Eclipse EDC Management API.")
public class SecretValue {
    @Schema(
        description = "Secret Name / Vault Key Name.",
        example = "myApiAuthKey",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String secretName;

    @Schema(
        description = "Raw inline Value.",
        example = "<auth key here>",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String rawValue;
}
