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
package de.sovity.edc.ce.libs.mappers.policy;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ce.api.common.model.UiPolicyLiteral;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.policy.model.Expression;
import org.eclipse.edc.policy.model.LiteralExpression;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LiteralMapper {
    private final ObjectMapper jsonLdObjectMapper;

    @SneakyThrows
    public Object getUiLiteralValue(UiPolicyLiteral literal) {
        if (literal == null) {
            return null;
        }

        return switch (literal.getType()) {
            case STRING -> literal.getValue();
            case STRING_LIST -> literal.getValueList();
            case JSON -> jsonLdObjectMapper.readValue(literal.getValue(), Object.class);
        };
    }

    public Optional<String> getExpressionString(
        Expression expression,
        MappingErrors errors
    ) {
        return getLiteralExpression(expression, errors).flatMap(literalExpression ->
            getLiteralExpressionString(literalExpression, errors));
    }

    public Optional<UiPolicyLiteral> getExpressionValue(
        Expression expression,
        MappingErrors errors
    ) {
        return getLiteralExpression(expression, errors).flatMap(this::getLiteralExpressionValue);
    }

    private Optional<String> getLiteralExpressionString(
        LiteralExpression literalExpression,
        MappingErrors errors
    ) {
        var value = literalExpression.getValue();
        if (value == null) {
            errors.forChildObject("value").add("Is not a string, but null.");
            return Optional.empty();
        }

        if (!(value instanceof String)) {
            errors.forChildObject("value").add("Is not a string.");
            return Optional.empty();
        }

        return Optional.of((String) value);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private Optional<UiPolicyLiteral> getLiteralExpressionValue(LiteralExpression literalExpression) {
        Object value = literalExpression.getValue();
        boolean isString = value instanceof String;
        if (isString) {
            return Optional.of(UiPolicyLiteral.ofString((String) value));
        }

        boolean isStringList = value instanceof Collection<?> && ((Collection<?>) value).stream()
            .allMatch(it -> it == null || it instanceof String);
        if (isStringList) {
            return Optional.of(UiPolicyLiteral.ofStringList((Collection<String>) value));
        }

        String json = jsonLdObjectMapper.writeValueAsString(value);
        return Optional.of(UiPolicyLiteral.ofJson(json));
    }


    Optional<LiteralExpression> getLiteralExpression(Expression expression, MappingErrors errors) {
        if (expression == null) {
            errors.add("Expression is null.");
            return Optional.empty();
        }

        if (!(expression instanceof LiteralExpression)) {
            errors.add("Expression type is not LiteralExpression, but %s.".formatted(expression.getClass().getName()));
            return Optional.empty();
        }

        return Optional.of((LiteralExpression) expression);
    }
}
