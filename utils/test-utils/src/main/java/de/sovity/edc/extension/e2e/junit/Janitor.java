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

package de.sovity.edc.extension.e2e.junit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Janitor {

    private List<Runnable> hooks = new ArrayList<>();

    public void clear() {
        Collections.reverse(hooks);
        hooks.forEach(Runnable::run);
        hooks.clear();
    }

    public void afterEach(Runnable runnable) {
        hooks.add(runnable);
    }
}
