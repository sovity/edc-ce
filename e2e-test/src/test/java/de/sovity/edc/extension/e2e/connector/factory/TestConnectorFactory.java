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
import de.sovity.edc.extension.e2e.db.TestDatabase;
import org.eclipse.edc.junit.extensions.EdcExtension;

public interface TestConnectorFactory {

    Connector createConnector(String participantId, EdcExtension edcExtension, TestDatabase testDatabase);

}
