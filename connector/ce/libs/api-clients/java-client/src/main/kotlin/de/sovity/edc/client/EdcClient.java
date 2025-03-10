/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
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
