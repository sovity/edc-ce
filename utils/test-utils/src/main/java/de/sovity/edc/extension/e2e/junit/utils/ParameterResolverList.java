/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.extension.e2e.junit.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ParameterResolverList implements ParameterResolver {
    private final List<ParameterResolver> resolvers = new ArrayList<>();

    public void add(ParameterResolver resolver) {
        resolvers.add(resolver);
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return resolvers.stream().anyMatch(r -> r.supportsParameter(parameterContext, extensionContext));
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return resolvers.stream()
            .filter(r -> r.supportsParameter(parameterContext, extensionContext))
            .findFirst()
            .orElseThrow(() -> new ParameterResolutionException(
                "No resolver found for type %s".formatted(parameterContext.getParameter().getType().getName())
            ))
            .resolveParameter(parameterContext, extensionContext);
    }
}
