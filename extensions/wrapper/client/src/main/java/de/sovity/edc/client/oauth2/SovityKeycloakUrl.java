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
