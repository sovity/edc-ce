package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supported Data Source Types by UiDataSource", enumAsRef = true)
public enum UiDataSourceType {
    HTTP_DATA,
    ON_REQUEST,
    CUSTOM
}

