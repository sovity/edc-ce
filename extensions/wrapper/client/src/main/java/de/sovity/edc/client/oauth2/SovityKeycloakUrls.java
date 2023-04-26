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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SovityKeycloakUrls {
    public static final String PRODUCTION = "https://keycloak.prod-sovity.azure.sovity.io/realms/Portal/protocol/openid-connect/token";
    public static final String STAGING = "https://keycloak.stage-sovity.azure.sovity.io/realms/Portal/protocol/openid-connect/token";
}
