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
import de.sovity.edc.extension.e2e.connector.config.api.auth.ApiKeyAuthProvider;
import de.sovity.edc.extension.e2e.connector.config.api.auth.NoneAuthProvider;
import de.sovity.edc.extension.e2e.connector.config.part.EdcApiGroupConfigPart;

import java.util.Map;

import static de.sovity.edc.extension.e2e.TransferTestVariables.EDC_MANAGEMENT_AUTH_HEADER;
import static de.sovity.edc.extension.e2e.TransferTestVariables.EDC_MANAGEMENT_AUTH_VALUE;
import static de.sovity.edc.extension.e2e.TransferTestVariables.EDC_MANAGEMENT_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.EDC_PROTOCOL_URL;
import static de.sovity.edc.extension.e2e.env.EnvUtil.getUriFromEnv;
import static de.sovity.edc.extension.e2e.env.EnvUtil.loadRequiredVariable;

public class EnvRemoteConnectorFactory {

    public static ConnectorRemoteConfig connectorRemoteConfig(String participantId) {
        return new ConnectorRemoteConfig(
                participantId,
                Map.of(
                        EdcApiGroup.Management, getManagementApiGroupConfig(participantId),
                        EdcApiGroup.Protocol, getProtocolApiGroupConfig(participantId)));
    }

    private static EdcApiGroupConfigPart getProtocolApiGroupConfig(String participantId) {
        return EdcApiGroupConfigPart.protocolFromUri(
                getUriFromEnv(getVariableNameForParticipant(participantId, EDC_PROTOCOL_URL)),
                new NoneAuthProvider());
    }

    private static EdcApiGroupConfigPart getManagementApiGroupConfig(String participantId) {
        return EdcApiGroupConfigPart.mgntFromUri(
                getUriFromEnv(getVariableNameForParticipant(participantId, EDC_MANAGEMENT_URL)),
                new ApiKeyAuthProvider(
                        loadVariableForParticipant(participantId, EDC_MANAGEMENT_AUTH_HEADER),
                        loadVariableForParticipant(participantId, EDC_MANAGEMENT_AUTH_VALUE)));
    }

    private static String loadVariableForParticipant(
            String participantId,
            String variableTemplate) {
        return loadRequiredVariable(getVariableNameForParticipant(participantId, variableTemplate));
    }

    private static String getVariableNameForParticipant(
            String participantId,
            String variableTemplate) {
        return String.format(variableTemplate, participantId);
    }
}
