package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supported HTTP Methods by UiDataSource", enumAsRef = true)
public enum UiDataSourceHttpDataMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
}

