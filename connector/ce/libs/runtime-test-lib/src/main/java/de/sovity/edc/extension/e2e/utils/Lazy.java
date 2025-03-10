/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.utils;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class Lazy<T> {
    private final Supplier<T> supplier;

    private T tt;

    public synchronized T get() {
        if (tt == null) {
            tt = supplier.get();
        }

        return tt;
    }

    public boolean isInitialized() {
        return tt != null;
    }
}
