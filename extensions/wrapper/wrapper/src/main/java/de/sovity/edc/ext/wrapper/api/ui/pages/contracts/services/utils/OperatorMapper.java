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
    public OperatorDto fromString(String operator) {
        return OperatorDto.valueOf(operator.toUpperCase());

    }

    public Operator toOperator(OperatorDto operatorDto) {
        return Operator.valueOf(operatorDto.name());
    }
}
