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

package de.sovity.edc.ext.wrapper.api.usecase;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.eclipse.edc.connector.dataplane.selector.spi.store.DataPlaneInstanceStore;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.eclipse.edc.spi.protocol.ProtocolWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static de.sovity.edc.ext.wrapper.TestUtils.createConfiguration;
import static de.sovity.edc.ext.wrapper.TestUtils.givenManagementEndpoint;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

@ApiTest
@ExtendWith(EdcExtension.class)
class SupportedPolicyApiTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.registerServiceMock(ProtocolWebhook.class, mock(ProtocolWebhook.class));
        extension.registerServiceMock(JsonLd.class, mock(JsonLd.class));
        extension.setConfiguration(createConfiguration());
    }

    static ValidatableResponse whenSupportedPolicyFunctions() {
        return givenManagementEndpoint()
                .when()
                .get("/wrapper/use-case-api/supported-policy-functions")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void supportedPolicies() {
        whenSupportedPolicyFunctions()
                .assertThat()
                .body(equalTo("[\"ALWAYS_TRUE\",\"https://w3id.org/edc/v0.0.1/ns/inForceDate\"]"));
    }
}
