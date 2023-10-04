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

package de.sovity.edc.ext.wrapper.api.usecase;

import de.sovity.edc.ext.wrapper.api.usecase.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.KpiResult;
import de.sovity.edc.ext.wrapper.api.usecase.services.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.OfferingService;
import de.sovity.edc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * Provides the endpoints for use-case specific requests.
 *
 * @author Ronja Quensel (ronja.quensel@isst.fraunhofer.de)
 * @author Richard Treier
 * @author Tim Dahlmanns
 */
@RequiredArgsConstructor
public class UseCaseResourceImpl implements UseCaseResource {
    private final KpiApiService kpiApiService;
    private final SupportedPolicyApiService supportedPolicyApiService;
    private final OfferingService offeringService;

    @Override
    public void createOfferEndpoint(CreateOfferingDto dto) {
        offeringService.create(dto);
    }

    @Override
    public KpiResult getKpiEndpoint() {
        return kpiApiService.kpiEndpoint();
    }

    @Override
    public List<String> getSupportedFunctions() {
        return supportedPolicyApiService.getSupportedFunctions();
    }
}
