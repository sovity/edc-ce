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

package de.sovity.edc.extension.jwks.controller;


import de.sovity.edc.extension.jwks.JwksExtension;
import io.restassured.http.ContentType;
import org.eclipse.edc.connector.dataplane.selector.spi.store.DataPlaneInstanceStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.eclipse.edc.spi.security.Vault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static de.sovity.edc.extension.jwks.util.TestCertFromFileUtil.getCertStringFromFile;
import static io.restassured.RestAssured.given;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

@ExtendWith(EdcExtension.class)
public class EdcJwksControllerTest {

    private static final String AUTH_KEY = UUID.randomUUID().toString();
    private static final int WEB_HTTP_PORT = getFreePort();
    private static final String WEB_HTTP_PATH = "/api";

    private static final String CERTIFICATE_VAULT_ALIAS = "transfer-proxy";

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
        extension.registerServiceMock(
                DataPlaneInstanceStore.class,
                mock(DataPlaneInstanceStore.class));
        extension.setConfiguration(Map.of(
                "web.http.port", String.valueOf(WEB_HTTP_PORT),
                "web.http.path", WEB_HTTP_PATH,
                "web.http.management.port", String.valueOf(getFreePort()),
                "web.http.management.path", "/api/v1/data",
                "edc.api.auth.key", AUTH_KEY,
                "edc.last.commit.info", "test env commit message",
                "edc.build.date", "2023-05-08T15:30:00Z",
                JwksExtension.CERTIFICATE_ALIAS, CERTIFICATE_VAULT_ALIAS));
    }

    @Test
    void jwksSuccessfullyExposed(Vault vault) throws IOException {
        vault.storeSecret(CERTIFICATE_VAULT_ALIAS, getCertStringFromFile());
        var request = given()
                .baseUri("http://localhost:" + WEB_HTTP_PORT)
                .basePath(WEB_HTTP_PATH)
                .when()
                .get(JwksController.JWKS_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);

        request.assertThat()
                .body("keys[0].kty", equalTo("RSA"))
                .body("keys[0].e", equalTo("AQAB"))
                .body("keys[0].n", notNullValue())
                .body("keys[0].kid", equalTo("360586573322806545473834353174745870260060531097"))
                .body("keys[0].'x5t#S256'", equalTo("P-dbyBaTkocsAKpv0Lx3JHaOTEyPOclVNOdoi-hQ75o"))
                .body("keys[0].nbf", equalTo(1701353600))
                .body("keys[0].exp", equalTo(4854953600L))
                .body("keys[0].x5c", notNullValue());

    }

    @Test
    void certificateCannotBeLoadedFromVault() {
        given()
                .baseUri("http://localhost:" + WEB_HTTP_PORT)
                .basePath(WEB_HTTP_PATH)
                .when()
                .get(JwksController.JWKS_PATH)
                .then()
                .statusCode(500);
    }
}
