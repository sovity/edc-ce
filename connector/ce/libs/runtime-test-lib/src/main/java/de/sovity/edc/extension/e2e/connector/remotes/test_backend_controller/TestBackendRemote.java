/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.connector.remotes.test_backend_controller;

import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static de.sovity.edc.extension.e2e.connector.remotes.management_api.DataTransferTestUtil.buildDataAddressJsonLd;
import static de.sovity.edc.extension.e2e.connector.remotes.management_api.DataTransferTestUtil.buildDataAddressProperties;

@RequiredArgsConstructor
public class TestBackendRemote {
    private final String defaultApiUrl;

    public String getDataSinkUrl() {
        return getMockBackendUrl("data-sink");
    }

    public String getDataSinkSpyUrl() {
        return getMockBackendUrl("data-sink/spy");
    }

    public String getDataSourceUrl(String data) {
        return getMockBackendUrl("data-source?data=%s".formatted(data));
    }

    public String getDataSourceQueryParamsUrl() {
        return getMockBackendUrl("data-source/echo-query-params");
    }

    public String getMockBackendUrl(String path) {
        return "%s/test-backend/%s".formatted(defaultApiUrl, path);
    }

    public JsonObject getDataSinkJsonLd() {
        return buildDataAddressJsonLd(getDataSinkUrl(), "PUT");
    }

    public Map<String, String> getDataSinkProperties() {
        return buildDataAddressProperties(getDataSinkUrl(), "PUT");
    }
}
