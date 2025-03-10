/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.connector.remotes.api_wrapper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An asset that uses a mocked network resource.
 *
 * @param assetId The related asset's ID
 * @param networkAccesses How many times the resource was accessed via the network.
 */
public record MockedAsset(
    String assetId,
    AtomicInteger networkAccesses
) {
}
