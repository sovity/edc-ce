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
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static de.sovity.edc.ext.wrapper.TestUtils.createConfiguration;
import static org.hamcrest.Matchers.equalTo;

@ApiTest
@ExtendWith(EdcExtension.class)
class TestApiTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration(""));
    }

    @Test
    void test() {
        var request = TestUtils.test(new ExampleQuery("a", List.of("b")));
        request.assertThat()
                .body("name", equalTo("a"))
                .body("list[0].name", equalTo("b"));
    }
}
