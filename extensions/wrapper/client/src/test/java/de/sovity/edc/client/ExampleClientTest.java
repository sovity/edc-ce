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
package de.sovity.edc.client;

import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import org.apache.commons.lang3.Validate;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ApiTest
@ExtendWith(EdcExtension.class)
class ExampleClientTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(TestUtils.createConfiguration());
    }

    @Test
    void exampleEndpoint() {
        var client = EdcClient.builder()
                .dataManagementUrl(TestUtils.DMGMT_ENDPOINT)
                .dataManagementApiKey(TestUtils.DMGMT_API_KEY)
                .build();

        var result = client.exampleClient().exampleEndpoint(new ExampleQuery("a", List.of("b")));

        Validate.isTrue(result.getName().equals("a"));
        Validate.isTrue(result.getMyNestedList().get(0).getName().equals("b"));
    }
}
