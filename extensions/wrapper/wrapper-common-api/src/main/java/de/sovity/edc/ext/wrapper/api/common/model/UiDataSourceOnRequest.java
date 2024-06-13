
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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "ON_REQUEST type Data Source.")
public class UiDataSourceOnRequest {
    @Schema(
        description = "Contact E-Mail address",
        example = "contact@my-org.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String contactEmail;

    @Schema(
        description = "Contact Preferred E-Mail Subject",
        example = "Department XYZ Data Offer Request - My Product, My API",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String contactPreferredEmailSubject;
}
