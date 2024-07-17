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

package de.sovity.edc.extension.e2e.extension;

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
