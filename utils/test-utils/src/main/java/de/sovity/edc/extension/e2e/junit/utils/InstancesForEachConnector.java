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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;


public class InstancesForEachConnector<S> implements ParameterResolver {
    private final List<S> sides;
    private final BiFunction<ParameterContext, ExtensionContext, S> getSideOrNull;
    private final Map<S, InstancesForJunitTest> instances;

    public InstancesForEachConnector(List<S> sides, BiFunction<ParameterContext, ExtensionContext, S> getSideOrNull) {
        this.sides = sides;
        this.getSideOrNull = getSideOrNull;
        instances = sides.stream()
            .collect(toMap(identity(), unused -> new InstancesForJunitTest()));
    }

    public <T> List<T> all(Class<T> clazz) {
        return sides.stream().flatMap(side -> forSide(side).all(clazz).stream()).toList();
    }

    public InstancesForJunitTest forSide(S side) {
        var instancesOfSide = instances.get(side);
        if (instancesOfSide == null) {
            throw new IllegalArgumentException("No instances for side %s found".formatted(side));
        }
        return instancesOfSide;
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        var side = getSideOrNull.apply(parameterContext, extensionContext);
        if (side == null) {
            return false;
        }

        return forSide(side).supportsParameter(parameterContext, extensionContext);
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        var side = getSideOrNull.apply(parameterContext, extensionContext);
        if (side == null) {
            throw new ParameterResolutionException("No side found for parameter %s".formatted(parameterContext.getParameter()));
        }

        return forSide(side).resolveParameter(parameterContext, extensionContext);
    }
}
