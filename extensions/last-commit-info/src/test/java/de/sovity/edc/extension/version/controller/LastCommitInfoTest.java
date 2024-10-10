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
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.version.controller;

import de.sovity.edc.extension.e2e.junit.CeIntegrationTestExtension;
import de.sovity.edc.utils.config.ConfigProps;
import de.sovity.edc.utils.config.ConfigUtils;
import io.restassured.http.ContentType;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

@ApiTest
class LastCommitInfoTest {

    @RegisterExtension
    static CeIntegrationTestExtension extension = CeIntegrationTestExtension.builder()
        .skipDb(true)
        .configOverrides(config -> config
            .property(ConfigProps.EDC_LAST_COMMIT_INFO, "test env commit message")
            .property(ConfigProps.EDC_BUILD_DATE, "2023-05-08T15:15:00Z")
        )
        .build();

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
    }

    @Test
    void testEnvAndJar(Config config) {
        var request = given()
            .baseUri(ConfigUtils.getManagementApiUrl(config))
            .header("X-Api-Key", ConfigUtils.getManagementApiKey(config))
            .when()
            .contentType(ContentType.JSON)
            .get("/last-commit-info")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON);

        request.assertThat()
            .body("envLastCommitInfo", equalTo("test env commit message"))
            .body("jarLastCommitInfo", equalTo("test jar commit message"))
            .body("envBuildDate", equalTo("2023-05-08T15:15:00Z"))
            .body("jarBuildDate", equalTo("2023-05-09T15:30:00Z"));

    }
}
