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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.extension.e2e.connector.remotes.api_wrapper;

import de.sovity.edc.utils.config.ConfigUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.edc.spi.system.configuration.Config;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiWrapperRemoteConfig {
    @NonNull
    private final String providerParticipantId;

    @NonNull
    private final String providerProtocolApiUrl;

    public static ApiWrapperRemoteConfig forProviderConfig(Config providerConfig) {
        var providerConfigEntries = providerConfig.getEntries();
        return builder()
            .providerParticipantId(ConfigUtils.getParticipantId(providerConfigEntries))
            .providerProtocolApiUrl(ConfigUtils.getManagementApiUrl(providerConfigEntries))
            .build();
    }
}
