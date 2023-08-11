/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.contracts.services.utils;
import de.sovity.edc.ext.wrapper.api.ui.model.OperatorDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.Operator;

@RequiredArgsConstructor
public class OperatorMapper {
    public static OperatorDto fromString(String operator) {
        OperatorDto operatorDto;
        switch (operator.toUpperCase()) {
            case "EQ":
                operatorDto = OperatorDto.EQ;
                break;
            case "NEQ":
                operatorDto = OperatorDto.NEQ;
                break;
            case "GT":
                operatorDto = OperatorDto.GT;
                break;
            case "GEQ":
                operatorDto = OperatorDto.GEQ;
                break;
            case "LT":
                operatorDto = OperatorDto.LT;
                break;
            case "LEQ":
                operatorDto = OperatorDto.LEQ;
                break;
            case "IN":
                operatorDto = OperatorDto.IN;
                break;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
        return operatorDto;

    }
    public OperatorDto toOperatorDto(Operator operator) {
        return fromString(operator.getOdrlRepresentation());
    }

    public Operator toOperator(OperatorDto operatorDto) {
        return Operator.valueOf(operatorDto.name());
    }
}
