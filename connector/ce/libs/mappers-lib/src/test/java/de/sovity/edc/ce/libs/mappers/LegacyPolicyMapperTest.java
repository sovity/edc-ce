/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers;

import de.sovity.edc.ce.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ce.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.ce.api.common.model.UiPolicyExpression;
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
        UiPolicyCreateRequest request = null;

        // act
        UiPolicyExpression result = legacyPolicyMapper.buildUiPolicyExpression(request);

        // assert
        assertThat(result).isEqualTo(UiPolicyExpression.empty());
    }

    @Test
    void buildUiPolicyExpression_expressionsNull() {
        // arrange
        var request = new UiPolicyCreateRequest();
        request.setConstraints(null);

        // act
        UiPolicyExpression result = legacyPolicyMapper.buildUiPolicyExpression(request);

        // assert
        assertThat(result).isEqualTo(UiPolicyExpression.empty());
    }

    @Test
    void buildUiPolicyExpression_emptyExpressions() {
        // arrange
        var request = new UiPolicyCreateRequest();
        request.setConstraints(List.of());

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
        request.setConstraints(List.of(expression));

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
        request.setConstraints(List.of(constraint1, constraint2));

        // act
        UiPolicyExpression result = legacyPolicyMapper.buildUiPolicyExpression(request);

        // assert
        assertThat(result).isEqualTo(UiPolicyExpression.and(List.of(
            UiPolicyExpression.constraint(constraint1),
            UiPolicyExpression.constraint(constraint2)
        )));
    }
}
