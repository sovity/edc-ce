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
import de.sovity.edc.extension.e2e.connector.config.TestConnectorConfigFactory;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.junit.extensions.EdcExtension;

import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.MANAGEMENT;
import static de.sovity.edc.extension.e2e.connector.config.api.EdcApiGroup.PROTOCOL;

@RequiredArgsConstructor
public class TestConnectorFactoryImpl implements TestConnectorFactory {

    private final TestConnectorConfigFactory testConnectorConfigFactory;

    @Override
    public Connector createConnector(
            String participantId,
            EdcExtension edcExtension,
            TestDatabase testDatabase) {
        var edcConfig = testConnectorConfigFactory.getConfig(participantId, testDatabase);
        var connector = TestConnector.builder()
                .participantId(participantId)
                .managementApiGroupConfig(edcConfig.getApiGroupConfig(MANAGEMENT))
                .protocolApiGroupConfig(edcConfig.getApiGroupConfig(PROTOCOL))
                .build();
        edcExtension.setConfiguration(edcConfig.getConfigAsMap());
        return connector;
    }

}
