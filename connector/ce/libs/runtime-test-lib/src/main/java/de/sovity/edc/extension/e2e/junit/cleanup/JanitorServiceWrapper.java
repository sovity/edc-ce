/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.cleanup;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.services.spi.asset.AssetService;
import org.eclipse.edc.spi.result.ServiceResult;

@RequiredArgsConstructor
public class JanitorServiceWrapper {

    private final Janitor janitor;
    private final AssetService service;

    public ServiceResult<Asset> create(Asset asset) {
        val result = service.create(asset);
        janitor.afterEach(() -> service.delete(asset.getId()));
        return result;
    }
}
