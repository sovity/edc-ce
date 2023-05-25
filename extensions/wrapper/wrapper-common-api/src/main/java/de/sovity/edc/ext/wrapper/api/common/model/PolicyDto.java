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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Opinionated subset of the EDC policy.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Type-Safe OpenAPI generator friendly Policy DTO that supports an opinionated"
        + " subset of the original EDC Policy Entity.")
public class PolicyDto {
    @Schema(description = "Legacy JSON as built by the Management API. Will be replaced "
            + "in the future by a type-safe variant without polymorphisms that can be used "
            + "for our generated clients.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Object legacyPolicy;

    @Schema(description = "Type of the policy. Usually 'SET'",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("@type")
    @NonNull
    private PolicyTypeDto type;

    @Schema(description = "Permission for this policy", requiredMode = RequiredMode.REQUIRED)
    @NonNull
    private PermissionDto permission;

    public PolicyDto(Object legacyPolicy) {
        this.legacyPolicy = legacyPolicy;
    }
}
