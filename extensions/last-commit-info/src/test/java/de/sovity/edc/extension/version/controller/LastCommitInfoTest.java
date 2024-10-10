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
import org.eclipse.edc.connector.dataplane.selector.spi.client.DataPlaneClientFactory;
import org.eclipse.edc.spi.system.configuration.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

class LastCommitInfoTest {

    @RegisterExtension
    static CeIntegrationTestExtension extension = CeIntegrationTestExtension.builder()
        .additionalModule(":extensions:last-commit-info")
        .additionalModule(":launchers:utils:vanilla-control-plane")
        .skipDb(true)
        .configOverrides(config -> config
            .property(ConfigProps.EDC_LAST_COMMIT_INFO, "test env commit message")
            .property(ConfigProps.EDC_BUILD_DATE, "2023-05-08T15:15:00Z")
        )
        .beforeEdcStartup(runtime -> {
            runtime.registerServiceMock(DataPlaneClientFactory.class, mock(DataPlaneClientFactory.class));
        })
        .build();

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
