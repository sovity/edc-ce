/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.e2e;

import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Edc07MigrationBaselineTest {

    @RegisterExtension
    private static CeIntegrationTestExtension e2eTestExtension = CeIntegrationTestExtension.builder()
        .additionalModule(":launchers:connectors:sovity-dev")
        .skipDb(false)
        .build();

    @SneakyThrows
    @Test
    void worked(Connection cnx) {
        // arrange

        // act
        cnx.createStatement().executeQuery("SELECT * from edc.public.edc_asset");
        cnx.createStatement().executeQuery("SELECT * from edc.public.sovity_contract_termination");

        // assert
        assertThrows(Exception.class, () -> cnx.createStatement().executeQuery("SELECT * from edc.public.doesntExist"));
    }
}
