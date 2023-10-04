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

package de.sovity.edc.client;

import de.sovity.edc.client.gen.api.EnterpriseEditionApi;
import de.sovity.edc.client.gen.api.UiApi;
import de.sovity.edc.client.gen.api.UseCaseApi;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * API Client for our EDC API Wrapper.
 */
@Value
@Accessors(fluent = true)
public class EdcClient {
    UiApi uiApi;
    UseCaseApi useCaseApi;
    EnterpriseEditionApi enterpriseEditionApi;

    public static EdcClientBuilder builder() {
        return new EdcClientBuilder();
    }

    public void testConnection() {
        useCaseApi.getKpis();
    }
}
