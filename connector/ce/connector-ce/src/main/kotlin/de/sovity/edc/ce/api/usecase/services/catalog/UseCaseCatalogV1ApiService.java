/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.usecase.services.catalog;

import de.sovity.edc.ce.api.ui.model.UiDataOffer;
import de.sovity.edc.ce.api.ui.pages.catalog.UiDataOfferBuilder;
import de.sovity.edc.ce.api.usecase.model.CatalogQuery;
import de.sovity.edc.ce.libs.mappers.dsp.DspCatalogService;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.List;

@RequiredArgsConstructor
@Service
@Deprecated
public class UseCaseCatalogV1ApiService {
    private final UiDataOfferBuilder uiDataOfferBuilder;
    private final DspCatalogService dspCatalogService;
    private final FilterExpressionMapper filterExpressionMapper;

    public List<UiDataOffer> fetchDataOffers(CatalogQuery catalogQuery) {
        var querySpec = buildQuerySpec(catalogQuery);

        var dspCatalog = dspCatalogService.fetchDataOffersWithFilters(
            catalogQuery.getParticipantId(),
            catalogQuery.getConnectorEndpoint(),
            querySpec);
        return uiDataOfferBuilder.buildUiDataOffers(dspCatalog);
    }

    private QuerySpec buildQuerySpec(CatalogQuery params) {
        var builder = QuerySpec.Builder.newInstance()
            .limit(withDefault(params.getLimit(), Integer.MAX_VALUE))
            .offset(withDefault(params.getOffset(), 0));

        var filterExpressions = params.getFilterExpressions();
        if (filterExpressions != null && !filterExpressions.isEmpty()) {
            builder.filter(filterExpressionMapper.buildCriteria(filterExpressions));
        }

        return builder.build();
    }

    private <T> T withDefault(T valueOrNull, T orElse) {
        return valueOrNull == null ? orElse : valueOrNull;
    }
}
