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


import de.sovity.edc.ext.wrapper.api.ui.model.CriterionDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.query.Criterion;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ContractDefinitionUtils {

    public CriterionDto mapToCriterionDto(Criterion criterion) {
        if (criterion == null) {
            return null;
        }

        CriterionDto dto = new CriterionDto();
        dto.setOperandLeft(String.valueOf(criterion.getOperandLeft()));
        dto.setOperator(criterion.getOperator());
        dto.setOperatorRight(String.valueOf(criterion.getOperandRight()));

        // Since the operatorRightArray and type fields don't exist in the Criterion object
        // they can't be mapped in this function

        return dto;
    }

    public List<CriterionDto> mapToCriterionDtos(List<Criterion> criteria) {
        if (criteria == null) {
            return null;
        }

        return criteria.stream().map(this::mapToCriterionDto).collect(Collectors.toList());
    }


}
