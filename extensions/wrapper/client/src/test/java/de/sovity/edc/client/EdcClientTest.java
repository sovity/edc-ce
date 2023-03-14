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
class EdcClientTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(TestUtils.createConfiguration(""));
    }

    @Test
    void test() {
        var client = EdcClientBuilder.newClient(TestUtils.DMGMT_ENDPOINT, TestUtils.DMGMT_API_KEY);

        var result = client.exampleClient().example(new ExampleQuery("a", List.of("b")));

        Validate.isTrue(result.getName().equals("a"));
        Validate.isTrue(result.getList().get(0).getName().equals("b"));
    }
}
