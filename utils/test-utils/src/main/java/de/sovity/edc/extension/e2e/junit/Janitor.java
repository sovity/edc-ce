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

import de.sovity.edc.client.EdcClient;
import lombok.val;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;

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

    public JanitorServiceWrapper withAssetService(AssetService assetService) {
        return new JanitorServiceWrapper(this, assetService);
    }
}
