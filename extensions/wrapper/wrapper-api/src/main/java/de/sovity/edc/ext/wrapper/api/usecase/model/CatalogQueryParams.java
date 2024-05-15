package de.sovity.edc.ext.wrapper.api.usecase.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Catalog query parameters")
public class CatalogQueryParams {
    @Schema(description = "Target EDC DSP endpoint URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetEdc;

    @Schema(description = "Limit the number of results", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer limit = Integer.MAX_VALUE;

    @Schema(description = "Offset for returned results, e.g. start at result 2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer offset = 0;

    @Schema(description = "Filter expression for catalog filtering", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CatalogFilterExpression filterExpression;
}
