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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions;


import de.sovity.edc.ext.wrapper.api.common.mappers.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionDto;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteralDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.query.Criterion;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CriterionMapper {
    private final OperatorMapper operatorMapper;

    public List<UiCriterionDto> mapToCriterionDtos(@NonNull List<Criterion> criteria) {
        return criteria.stream().map(this::mapToCriterionDto).toList();
    }

     UiCriterionDto mapToCriterionDto(Criterion criterion) {
        if (criterion == null) {
            return null;
        }
        UiCriterionDto dto = new UiCriterionDto();
        UiCriterionLiteralDto literalDto = buildCriterionLiteral(criterion.getOperandRight());
        dto.setOperandLeft(String.valueOf(criterion.getOperandLeft()));
        dto.setOperator(operatorMapper.getOperatorDto(criterion.getOperator()));
        dto.setOperandRight(literalDto);
        return dto;
    }

    public Criterion mapToCriterion(@NonNull UiCriterionDto criterionDto) {
        return new Criterion(
                criterionDto.getOperandLeft(),
                operatorMapper.getOperator(criterionDto.getOperator()).getOdrlRepresentation(),
                criterionDto.getOperandRight());
    }

    public List<Criterion> mapToCriteria(@NonNull List<UiCriterionDto> criterionDtos) {
        return criterionDtos.stream().map(this::mapToCriterion).toList();
    }


    UiCriterionLiteralDto buildCriterionLiteral(Object value) {
        if (value instanceof Collection) {
            var list = ((Collection<Object>) value).stream().map(it -> it == null ? null : it.toString()).toList();
            return UiCriterionLiteralDto.ofValueList(list);
        }

        return UiCriterionLiteralDto.ofValue(value.toString());
    }

    Object readCriterionLiteral(UiCriterionLiteralDto dto) {
        if (dto.getType() == UiCriterionLiteralDto.CriterionLiteralTypeDto.VALUE_LIST) {
            return dto.getValueList();
        }
        return dto.getValue();
    }
}
