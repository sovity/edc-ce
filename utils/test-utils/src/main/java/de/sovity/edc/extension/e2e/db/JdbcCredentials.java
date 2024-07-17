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

/**
 * JDBC Credentials
 *
 * @param jdbcUrl      JDBC URL without credentials
 * @param jdbcUser     JDBC User
 * @param jdbcPassword JDBC Password
 */
public record JdbcCredentials(
        String jdbcUrl,
        String jdbcUser,
        String jdbcPassword
) {
}
