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

package de.sovity.edc.ext.wrapper.api.ui.model;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
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
@Schema(description = "Polymorphic value. Could be a string, could be a string array, could be JSON elsewise.")
public class OperatorDto {
    @Schema(description = "Operator Enum Type", requiredMode = RequiredMode.REQUIRED)
    private OperatorEnumDto operatorEnumDto;

    public static OperatorDto fromString(String operator) {
        OperatorEnumDto operatorEnumDto;
        switch (operator.toUpperCase()) {
            case "EQ":
                operatorEnumDto = OperatorEnumDto.EQ;
                break;
            case "NEQ":
                operatorEnumDto = OperatorEnumDto.NEQ;
                break;
            case "GT":
                operatorEnumDto = OperatorEnumDto.GT;
                break;
            case "GEQ":
                operatorEnumDto = OperatorEnumDto.GEQ;
                break;
            case "LT":
                operatorEnumDto = OperatorEnumDto.LT;
                break;
            case "LEQ":
                operatorEnumDto = OperatorEnumDto.LEQ;
                break;
            case "IN":
                operatorEnumDto = OperatorEnumDto.IN;
                break;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }

        return OperatorDto.builder()
                .operatorEnumDto(operatorEnumDto)
                .build();
    }
}
