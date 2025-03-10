/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.connector.remotes.management_api;

import de.sovity.edc.runtime.config.AuthHeader;
import de.sovity.edc.runtime.config.ConfigUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ManagementApiConnectorRemoteConfig {
    @NonNull
    private final String participantId;

    @NonNull
    private final String defaultApiUrl;

    @NonNull
    private final String managementApiUrl;

    @Nullable
    private final Supplier<AuthHeader> managementApiAuthHeaderFactory;

    @NonNull
    private final String protocolApiUrl;

    @NonNull
    private final String publicApiUrl;

    public static ManagementApiConnectorRemoteConfig forConnector(
        ConfigUtils configUtils
    ) {
        return builder()
            .participantId(configUtils.getParticipantId())
            .managementApiUrl(configUtils.getManagementApiUrl())
            .managementApiAuthHeaderFactory(configUtils::getManagementApiAuthHeader)
            .protocolApiUrl(configUtils.getProtocolApiUrl())
            .publicApiUrl(configUtils.getPublicApiUrl())
            .defaultApiUrl(configUtils.getDefaultApiUrl())
            .build();
    }
}
