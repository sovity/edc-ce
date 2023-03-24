/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.implementation.usecase;

import de.sovity.edc.ext.wrapper.api.usecase.KpiResource;
import de.sovity.edc.ext.wrapper.api.usecase.model.KpiResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KpiResourceImpl implements KpiResource {
    private final KpiApiService kpiApiService;

    @Override
    public KpiResult kpiEndpoint() {
        return kpiApiService.kpiEndpoint();
    }
}
