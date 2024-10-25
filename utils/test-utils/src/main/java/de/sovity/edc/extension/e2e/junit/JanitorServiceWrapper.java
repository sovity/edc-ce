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
