/*
 *  Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial implementation
 *
 */

package de.sovity.edc.extension.e2e.db;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestDatabaseFactory {
    /**
     * Returns a JUnit 5 Extension that either connects to a test database or launches a
     * testcontainer.
     *
     * @return {@link TestDatabase}
     */
    public static TestDatabase getTestDatabase(int iDatabase) {
        if (TestDatabaseViaEnvVars.isSkipTestcontainers()) {
            return new TestDatabaseViaEnvVars(iDatabase);
        }

        return new TestDatabaseViaTestcontainers();
    }
}
