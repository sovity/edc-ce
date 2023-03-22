/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.client.examples;

import de.sovity.edc.client.EdcClient;
import de.sovity.edc.ext.wrapper.api.example.ExampleResource;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleItem;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
class GreetingResourceTest {
    @InjectMock
    EdcClient edcClient;

    ExampleResource exampleResource;

    @BeforeEach
    void setup() {
        exampleResource = mock(ExampleResource.class);
        when(edcClient.exampleClient()).thenReturn(exampleResource);
    }

    @Test
    void testHelloEndpoint() {
        when(exampleResource.exampleEndpoint(any())).thenReturn(new ExampleResult(
                "A",
                new ExampleItem("B"),
                List.of(new ExampleItem("C")),
                "http://my-ids-endpoint"
        ));

        given()
                .when().get("/")
                .then()
                .statusCode(200)
                .body(is("Backend-fetched IDS Endpoint: http://my-ids-endpoint"));
    }

}