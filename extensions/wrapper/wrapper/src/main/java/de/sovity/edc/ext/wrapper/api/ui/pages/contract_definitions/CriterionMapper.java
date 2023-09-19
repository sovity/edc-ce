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


import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterion;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteral;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.query.Criterion;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CriterionMapper {
    private final CriterionOperatorMapper criterionOperatorMapper;

    public List<UiCriterion> mapToCriterionDtos(@NonNull List<Criterion> criteria) {
        return criteria.stream().map(this::mapToCriterionDto).toList();
    }

    UiCriterion mapToCriterionDto(Criterion criterion) {
        if (criterion == null) {
            return null;
        }
        UiCriterion dto = new UiCriterion();
        UiCriterionLiteral literalDto = buildCriterionLiteral(criterion.getOperandRight());
        dto.setOperandLeft(String.valueOf(criterion.getOperandLeft()));
        dto.setOperator(criterionOperatorMapper.getUiCriterionOperator(criterion.getOperator()));
        dto.setOperandRight(literalDto);
        return dto;
    }

    public Criterion mapToCriterion(@NonNull UiCriterion criterionDto) {
        return new Criterion(
                criterionDto.getOperandLeft(),
                criterionOperatorMapper.getCriterionOperator(criterionDto.getOperator()),
                criterionDto.getOperandRight());
    }

    public List<Criterion> mapToCriteria(@NonNull List<UiCriterion> criterionDtos) {
        return criterionDtos.stream().map(this::mapToCriterion).toList();
    }


    UiCriterionLiteral buildCriterionLiteral(Object value) {
        if (value instanceof Collection) {
            var list = ((Collection<Object>) value).stream().map(it -> it == null ? null : it.toString()).toList();
            return UiCriterionLiteral.ofValueList(list);
        }

        return UiCriterionLiteral.ofValue(value.toString());
    }

    Object readCriterionLiteral(UiCriterionLiteral dto) {
        if (dto.getType() == UiCriterionLiteral.CriterionLiteralTypeDto.VALUE_LIST) {
            return dto.getValueList();
        }
        return dto.getValue();
    }
}
