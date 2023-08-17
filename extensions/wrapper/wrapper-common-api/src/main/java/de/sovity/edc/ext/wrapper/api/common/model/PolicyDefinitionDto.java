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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Opinionated subset of the EDC policy for our EDC UI.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@Schema(description = "Policy Definition as required for the Policy Definition Page")
public class PolicyDefinitionDto {

    @Schema(description = "UIPolicy Dto", requiredMode = Schema.RequiredMode.REQUIRED)
    private UiPolicyDto uiPolicyDto;
}

