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

package de.sovity.edc.extension.e2e.connector.factory;

import de.sovity.edc.extension.e2e.connector.Connector;
import de.sovity.edc.extension.e2e.connector.TestConnector;
import de.sovity.edc.extension.e2e.connector.config.api.auth.ApiKeyAuthProvider;
import de.sovity.edc.extension.e2e.connector.config.api.auth.NoneAuthProvider;
import de.sovity.edc.extension.e2e.connector.config.part.EdcApiGroupConfigPart;

import static de.sovity.edc.extension.e2e.TransferTestVariables.EDC_MANAGEMENT_AUTH_HEADER;
import static de.sovity.edc.extension.e2e.TransferTestVariables.EDC_MANAGEMENT_AUTH_VALUE;
import static de.sovity.edc.extension.e2e.TransferTestVariables.EDC_MANAGEMENT_URL;
import static de.sovity.edc.extension.e2e.TransferTestVariables.EDC_PROTOCOL_URL;
import static de.sovity.edc.extension.e2e.env.EnvUtil.getUriFromEnv;
import static de.sovity.edc.extension.e2e.env.EnvUtil.loadRequiredVariable;

public class EnvConnectorFactoryImpl implements EnvConnectorFactory {
    @Override
    public Connector createConnector(String participantId) {
        return TestConnector.builder()
                .participantId(participantId)
                .managementApiGroupConfig(getManagementApiGroupConfig(participantId))
                .protocolApiGroupConfig(getProtocolApiGroupConfig(participantId))
                .build();
    }

    private EdcApiGroupConfigPart getProtocolApiGroupConfig(String participantId) {
        return EdcApiGroupConfigPart.protocolFromUri(
                getUriFromEnv(getVariableNameForParticipant(participantId, EDC_PROTOCOL_URL)),
                new NoneAuthProvider());
    }

    private EdcApiGroupConfigPart getManagementApiGroupConfig(String participantId) {
        return EdcApiGroupConfigPart.mgntFromUri(
                getUriFromEnv(getVariableNameForParticipant(participantId, EDC_MANAGEMENT_URL)),
                new ApiKeyAuthProvider(
                        loadVariableForParticipant(participantId, EDC_MANAGEMENT_AUTH_HEADER),
                        loadVariableForParticipant(participantId, EDC_MANAGEMENT_AUTH_VALUE)));
    }

    private String loadVariableForParticipant(String participantId, String variableTemplate) {
        return loadRequiredVariable(getVariableNameForParticipant(participantId, variableTemplate));
    }

    private String getVariableNameForParticipant(String participantId, String variableTemplate) {
        return String.format(variableTemplate, participantId);
    }
}
