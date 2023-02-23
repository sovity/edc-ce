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
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.extension.version.controller;

import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.junit.extensions.EdcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static de.sovity.edc.extension.version.controller.TestUtils.createConfiguration;
import static de.sovity.edc.extension.version.controller.TestUtils.mockRequest;
import static org.hamcrest.Matchers.containsStringIgnoringCase;

@ApiTest
@ExtendWith(EdcExtension.class)
class LastCommitInfoEnvTest {

    @BeforeEach
    void setUp(EdcExtension extension) {
        extension.setConfiguration(createConfiguration("env"));
    }

    @Test
    void testEnvAndJar() {
        var request = mockRequest();
        request.assertThat().body(containsStringIgnoringCase("pipeline"));
        request.assertThat().body(containsStringIgnoringCase("env"));
    }
}
