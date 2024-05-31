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

import de.sovity.edc.ext.wrapper.api.common.model.UiPolicy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Trying to reduce an ODRL {@link org.eclipse.edc.policy.model.Policy} to a
 * {@link UiPolicy} is a lossful operation.
 * <p>
 * During the mapping errors can occur, as parts of the ODRL Policy aren't supported.
 * <p>
 * This class helps to collect those errors.
 */
@RequiredArgsConstructor
public class MappingErrors {
    @Getter
    private final List<String> errors;
    private final String path;

    public static MappingErrors root() {
        return new MappingErrors(new ArrayList<>(), "$");
    }

    public void add(String message) {
        errors.add("%s: %s".formatted(path, message));
    }

    public MappingErrors forChildObject(String name) {
        return new MappingErrors(errors, "%s.%s".formatted(path, name));
    }

    public MappingErrors forChildArrayElement(int index) {
        return new MappingErrors(errors, "%s[%d]".formatted(path, index));
    }
}
