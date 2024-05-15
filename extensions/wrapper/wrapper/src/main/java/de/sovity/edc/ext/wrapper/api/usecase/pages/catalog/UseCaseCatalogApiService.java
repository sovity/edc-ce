package de.sovity.edc.ext.wrapper.api.usecase.pages.catalog;

import de.sovity.edc.ext.wrapper.api.ui.model.UiDataOffer;
import de.sovity.edc.ext.wrapper.api.ui.pages.catalog.UiDataOfferBuilder;
import de.sovity.edc.ext.wrapper.api.usecase.model.CatalogQueryParams;
import de.sovity.edc.utils.catalog.DspCatalogService;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.query.QuerySpec;

import java.util.List;

@RequiredArgsConstructor
public class UseCaseCatalogApiService {
    private final UiDataOfferBuilder uiDataOfferBuilder;
    private final DspCatalogService dspCatalogService;
    private final FilterExpressionMapper filterExpressionMapper;

    public List<UiDataOffer> fetchDataOffers(CatalogQueryParams catalogQueryParams) {
        var querySpec = buildQuerySpec(catalogQueryParams);

        var dspCatalog = dspCatalogService.fetchDataOffersWithFilters(catalogQueryParams.getTargetEdc(), querySpec);
        return uiDataOfferBuilder.buildUiDataOffers(dspCatalog);
    }

    private QuerySpec buildQuerySpec(CatalogQueryParams params) {
        var builder = QuerySpec.Builder.newInstance()
                .limit(withDefault(params.getLimit(), Integer.MAX_VALUE))
                .offset(withDefault(params.getOffset(), 0));

        var filterExpression = params.getFilterExpression();
        if (filterExpression != null) {
            builder.filter(filterExpressionMapper.buildCriterion(filterExpression));
        }

        return builder.build();
    }

    private <T> T withDefault(T valueOrNull, T orElse) {
        return valueOrNull == null ? orElse : valueOrNull;
    }
}
