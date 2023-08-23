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

package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.Operator;

@RequiredArgsConstructor
public class OperatorMapper {
    public OperatorDto getOperatorDto(String operator) {
        return OperatorDto.valueOf(operator.toUpperCase());
    }

    public OperatorDto getOperatorDto(Operator operator) {
        return OperatorDto.valueOf(operator.name());
    }

    public Operator getOperator(OperatorDto operatorDto) {
        return Operator.valueOf(operatorDto.name());
    }
}
