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
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static de.sovity.edc.ext.wrapper.TestUtils.createConfiguration;
import static de.sovity.edc.ext.wrapper.TestUtils.givenManagementEndpoint;
import static org.hamcrest.Matchers.equalTo;

@ApiTest
@ExtendWith(EdcExtension.class)
class KpiApiTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration());
    }

    ValidatableResponse whenKpiEndpoint() {
        return givenManagementEndpoint()
                .when()
                .get("/wrapper/use-case-api/kpis")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void exampleEndpoint() {
        whenKpiEndpoint()
                .assertThat()
                .body("assetsCount", equalTo(0));
    }
}
