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


import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CriterionDto {
    private String operandLeft;
    private String operator;
    private String operatorRight;
    private String[] operatorRightArray;
    private CriterionType type;

    public static CriterionDto forString(String operandLeft, String operator, String operatorRight) {
        return new CriterionDto(operandLeft, operator, operatorRight, null, CriterionType.STRING);
    }

    public static CriterionDto forStringArray(String operandLeft, String operator, String[] operatorRightArray) {
        return new CriterionDto(operandLeft, operator, null, operatorRightArray, CriterionType.STRING_ARRAY);
    }

    public static CriterionDto forJson(String operandLeft, String operator, String operatorRight) {
        return new CriterionDto(operandLeft, operator, operatorRight, null, CriterionType.JSON);
    }

}

enum CriterionType {
    STRING, STRING_ARRAY, JSON
}
