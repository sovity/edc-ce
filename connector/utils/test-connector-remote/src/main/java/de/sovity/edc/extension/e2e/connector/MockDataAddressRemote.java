/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.extension.e2e.connector;

import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroupConfig;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.buildDataAddressJsonLd;
import static de.sovity.edc.extension.e2e.connector.DataTransferTestUtil.buildDataAddressProperties;

@RequiredArgsConstructor
public class MockDataAddressRemote {
    private final EdcApiGroupConfig defaultEndpoint;

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
        return "%s/test-backend/%s".formatted(defaultEndpoint.getUri().toString(), path);
    }

    public JsonObject getDataSinkJsonLd() {
        return buildDataAddressJsonLd(getDataSinkUrl(), "PUT");
    }

    public Map<String, String> getDataSinkProperties() {
        return buildDataAddressProperties(getDataSinkUrl(), "PUT");
    }
}
