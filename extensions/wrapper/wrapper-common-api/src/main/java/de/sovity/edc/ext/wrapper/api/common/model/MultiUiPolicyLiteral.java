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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder(toBuilder = true)
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Sum type: A String, a list of Strings or a generic JSON value.")
public class MultiUiPolicyLiteral {
    @Schema(description = "Value Type", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultiUiPolicyLiteralType type;

    @Schema(description = "Only for types STRING and JSON")
    private String value;

    @Schema(description = "Only for type STRING_LIST")
    private List<String> valueList;

    public static MultiUiPolicyLiteral ofString(String string) {
        return MultiUiPolicyLiteral.builder()
            .type(MultiUiPolicyLiteralType.STRING)
            .value(string)
            .build();
    }

    public static MultiUiPolicyLiteral ofJson(String jsonString) {
        return MultiUiPolicyLiteral.builder()
            .type(MultiUiPolicyLiteralType.JSON)
            .value(jsonString)
            .build();
    }

    public static MultiUiPolicyLiteral ofStringList(Collection<String> strings) {
        return MultiUiPolicyLiteral.builder()
            .type(MultiUiPolicyLiteralType.STRING_LIST)
            .valueList(new ArrayList<>(strings))
            .build();
    }
}
