/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteral;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.policy.model.Expression;
import org.eclipse.edc.policy.model.LiteralExpression;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
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
