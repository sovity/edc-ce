/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.cleanup;

import de.sovity.edc.client.EdcClient;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Automatically cleans up the allocated resources when the API or service is used via its wrappers.
 */
public class Janitor {

    private final List<Runnable> hooks = new ArrayList<>();

    public void clear() {
        Collections.reverse(hooks);
        for (val hook : hooks) {
            try {
                hook.run();
            } catch (Exception e) {
                // swallow. This may happen if an element is deleted twice for instance
                e.printStackTrace();
            }
        }
        hooks.clear();
    }

    public void afterEach(Runnable runnable) {
        hooks.add(runnable);
    }

    public JanitorApiWrapper withClient(EdcClient client) {
        return new JanitorApiWrapper(this, client);
    }
}
