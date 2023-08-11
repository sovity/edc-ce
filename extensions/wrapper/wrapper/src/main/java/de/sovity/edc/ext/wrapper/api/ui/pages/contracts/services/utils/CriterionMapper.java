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
import de.sovity.edc.ext.wrapper.api.ui.model.CriterionLiteralDto;
import de.sovity.edc.ext.wrapper.api.ui.model.OperatorDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.query.Criterion;

import java.util.List;

@RequiredArgsConstructor
public class CriterionMapper {
    private final OperatorMapper operatorMapper;

    public CriterionDto mapToCriterionDto(Criterion criterion) {
        if (criterion == null) {
            return null;
        }
        CriterionDto dto = new CriterionDto();
        CriterionLiteralDto literalDto = new CriterionLiteralDto();;
        literalDto.setValue((String) criterion.getOperandRight());
        dto.setOperandLeft(String.valueOf(criterion.getOperandLeft()));
        dto.setOperator(operatorMapper.fromString(criterion.getOperator()));
        dto.setOperandRight(literalDto);
        return dto;
    }

    public List<CriterionDto> mapToCriterionDtos(List<Criterion> criteria) {
        if (criteria == null) {
            return null;
        }

        return criteria.stream().map(this::mapToCriterionDto).toList();
    }

    public Criterion mapToCriterion(CriterionDto criterionDto) {
        if (criterionDto == null) {
            return null;
        }
        return new Criterion(
                criterionDto.getOperandLeft(),
                operatorMapper.toOperator(criterionDto.getOperator()).getOdrlRepresentation(),
                criterionDto.getOperandRight());
    }

    public List<Criterion> mapToCriteria(List<CriterionDto> criterionDtos) {
        if (criterionDtos == null) {
            return null;
        }
        return criterionDtos.stream().map(this::mapToCriterion).toList();
    }
}
