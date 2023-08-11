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

package de.sovity.edc.extension.e2e.connector.config;

import de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup;
import de.sovity.edc.extension.e2e.connector.config.part.EdcApiGroupConfigPart;

import java.util.Map;

public record ConnectorRemoteConfig(
        String participantId,
        Map<EdcApiGroup, EdcApiGroupConfigPart> apiGroupMap
) {

    public EdcApiGroupConfigPart getApiGroupConfigPart(EdcApiGroup apiGroup) {
        return apiGroupMap.get(apiGroup);
    }

    public EdcApiGroupConfigPart defaultApiGroupConfig() {
        return apiGroupMap.get(EdcApiGroup.Default);
    }

    public EdcApiGroupConfigPart managementApiGroupConfig() {
        return apiGroupMap.get(EdcApiGroup.Management);
    }

    public EdcApiGroupConfigPart protocolApiGroupConfig() {
        return apiGroupMap.get(EdcApiGroup.Protocol);
    }
}
