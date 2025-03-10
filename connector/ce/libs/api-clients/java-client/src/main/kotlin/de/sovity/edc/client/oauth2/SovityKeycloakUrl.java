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
package de.sovity.edc.client.oauth2;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Quick access to the Keycloak OAuth Token URLs for our staging and production environments.
 * <p>
 * For ease of use of our API Wrapper Client Libraries in Use Case Applications.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SovityKeycloakUrl {

    /**
     * Sovity Production Keycloak OAuth2 Token URL
     */
    public static final String PRODUCTION = "https://keycloak.prod-sovity.azure.sovity.io/realms/Portal/protocol/openid-connect/token";

    /**
     * Sovity Staging Keycloak OAuth2 Token URL
     */
    public static final String STAGING = "https://keycloak.stage-sovity.azure.sovity.io/realms/Portal/protocol/openid-connect/token";
}
