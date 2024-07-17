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

package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpression;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("deprecated")
@ExtendWith(MockitoExtension.class)
public class LegacyPolicyMapperTest {
    @InjectMocks
    LegacyPolicyMapper legacyPolicyMapper;

    @Test
    void buildUiPolicyExpression_null() {
        // arrange
        var request = (UiPolicyCreateRequest) null;

        // act
        UiPolicyExpression result = legacyPolicyMapper.buildUiPolicyExpression(request);

        // assert
        assertThat(result).isEqualTo(UiPolicyExpression.empty());
    }

    @Test
    void buildUiPolicyExpression_expressionsNull() {
        // arrange
        var request = new UiPolicyCreateRequest();
        request.setExpressions(null);

        // act
        UiPolicyExpression result = legacyPolicyMapper.buildUiPolicyExpression(request);

        // assert
        assertThat(result).isEqualTo(UiPolicyExpression.empty());
    }

    @Test
    void buildUiPolicyExpression_emptyExpressions() {
        // arrange
        var request = new UiPolicyCreateRequest();
        request.setExpressions(List.of());

        // act
        UiPolicyExpression result = legacyPolicyMapper.buildUiPolicyExpression(request);

        // assert
        assertThat(result).isEqualTo(UiPolicyExpression.empty());
    }

    @Test
    void buildUiPolicyExpression_singleExpression() {
        // arrange
        var request = new UiPolicyCreateRequest();
        var expression = new UiPolicyConstraint();
        request.setExpressions(List.of(expression));

        // act
        UiPolicyExpression result = legacyPolicyMapper.buildUiPolicyExpression(request);

        // assert
        assertThat(result).isEqualTo(UiPolicyExpression.constraint(expression));
    }

    @Test
    void buildUiPolicyExpression_multipleExpressions() {
        // arrange
        var request = new UiPolicyCreateRequest();
        var constraint1 = mock(UiPolicyConstraint.class);
        var constraint2 = mock(UiPolicyConstraint.class);
        request.setExpressions(List.of(constraint1, constraint2));

        // act
        UiPolicyExpression result = legacyPolicyMapper.buildUiPolicyExpression(request);

        // assert
        assertThat(result).isEqualTo(UiPolicyExpression.and(List.of(
            UiPolicyExpression.constraint(constraint1),
            UiPolicyExpression.constraint(constraint2)
        )));
    }
}
