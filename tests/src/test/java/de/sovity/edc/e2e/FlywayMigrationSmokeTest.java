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

import de.sovity.edc.extension.e2e.extension.CeE2eTestExtensionConfigFactory;
import de.sovity.edc.extension.e2e.extension.E2eTestExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class FlywayMigrationSmokeTest {

    @RegisterExtension
    private static E2eTestExtension e2eTestExtension = new E2eTestExtension(CeE2eTestExtensionConfigFactory.defaultBuilder().toBuilder()
        .consumerConfigCustomizer(it -> {
            it.setProperty("MY_EDC_PARTICIPANT_ID", "sovity-demo-a");

            it.setProperty("EDC_OAUTH_TOKEN_URL", "https://keycloak.stage-sovity.azure.sovity.io/realms/DAPS/protocol/openid-connect/token");
            it.setProperty("EDC_OAUTH_PROVIDER_JWKS_URL", "https://keycloak.stage-sovity.azure.sovity.io/realms/DAPS/protocol/openid-connect/certs");
            it.setProperty("EDC_OAUTH_CLIENT_ID", "74:BE:81:48:61:F9:0E:06:2D:E4:CB:96:2E:36:30:11:83:4C:32:29:keyid:EB:8E:86:7F:A2:B6:19:8A:10:58:46:CF:88:38:D6:F1:E1:E2:FC:69");

            it.setProperty("EDC_KEYSTORE", "/home/uuh/dev/sovity/edc-ce/server.keystore");
            it.setProperty("EDC_KEYSTORE_PASSWORD", "changeit");
            it.setProperty("EDC_OAUTH_CERTIFICATE_ALIAS", "1");
            it.setProperty("EDC_OAUTH_PRIVATE_KEY_ALIAS", "1");

            it.setProperty("EDC_LOGGINGHOUSE_EXTENSION_ENABLED", "'true'");
            it.setProperty("EDC_LOGGINGHOUSE_EXTENSION_URL", "https://webhook.site/5de03553-60fb-4654-a39f-86ab7bf98950");
            it.setProperty("EDC_DATASOURCE_LOGGINGHOUSE_URL", it.getWellKnownProperties().getDatabaseJdbcUrl());
            it.setProperty("EDC_DATASOURCE_LOGGINGHOUSE_USER", it.getWellKnownProperties().getDatabaseUser());
            it.setProperty("EDC_DATASOURCE_LOGGINGHOUSE_PASSWORD", it.getWellKnownProperties().getDatabasePassword());
        })
        .build());

    @Test
    void smoke() {
        // arrange

        // act

        // assert
    }
}
