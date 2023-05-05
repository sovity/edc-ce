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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Credentials for connecting to the EDC via the OAuth2 "Client Credentials" flow.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OAuth2ClientCredentials {
    @NonNull
    private String tokenUrl;
    @NonNull
    private String clientId;
    @NonNull
    private String clientSecret;
}
