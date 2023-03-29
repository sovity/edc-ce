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

package de.sovity.edc.ext.wrapper;

import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static de.sovity.edc.ext.wrapper.TestUtils.createConfiguration;
import static de.sovity.edc.ext.wrapper.TestUtils.givenManagementEndpoint;
import static org.hamcrest.Matchers.equalTo;

@ApiTest
@ExtendWith(EdcExtension.class)
class ExampleApiTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration());
    }

    ValidatableResponse whenExampleEndpoint(ExampleQuery exampleQuery) {
        return givenManagementEndpoint()
                .when()
                .contentType(ContentType.JSON)
                .body(exampleQuery)
                .post("/wrapper/example-api/example")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void exampleEndpoint() {
        whenExampleEndpoint(new ExampleQuery("a", List.of("b")))
                .assertThat()
                .body("name", equalTo("a"))
                .body("myNestedList[0].name", equalTo("b"));
    }
}
