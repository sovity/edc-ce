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

import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.model.UiDataOffer;
import de.sovity.edc.ext.wrapper.api.ui.pages.policy.PolicyDefinitionApiService;
import de.sovity.edc.ext.wrapper.api.usecase.model.CatalogQuery;
import de.sovity.edc.ext.wrapper.api.usecase.model.KpiResult;
import de.sovity.edc.ext.wrapper.api.usecase.model.PolicyCreateRequest;
import de.sovity.edc.ext.wrapper.api.usecase.pages.catalog.UseCaseCatalogApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.KpiApiService;
import de.sovity.edc.ext.wrapper.api.usecase.services.SupportedPolicyApiService;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * Provides the endpoints for use-case specific requests.
 */
@RequiredArgsConstructor
public class UseCaseResourceImpl implements UseCaseResource {
    private final KpiApiService kpiApiService;
    private final SupportedPolicyApiService supportedPolicyApiService;
    private final UseCaseCatalogApiService useCaseCatalogApiService;
    private final PolicyDefinitionApiService policyDefinitionApiService;

    @Override
    public KpiResult getKpis() {
        return kpiApiService.getKpis();
    }

    @Override
    public List<String> getSupportedFunctions() {
        return supportedPolicyApiService.getSupportedFunctions();
    }

    @Override
    public List<UiDataOffer> queryCatalog(CatalogQuery catalogQuery) {
        return useCaseCatalogApiService.fetchDataOffers(catalogQuery);
    }

    @Override
    public IdResponseDto createPolicyDefinitionUseCase(PolicyCreateRequest policyCreateRequest) {
        return policyDefinitionApiService.createPolicyDefinition(policyCreateRequest);
    }
}
