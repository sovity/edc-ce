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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static de.sovity.edc.extension.version.controller.TestUtils.createConfiguration;
import static de.sovity.edc.extension.version.controller.TestUtils.mockRequest;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

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
        request.assertThat().body(containsString("pipeline"));
        request.assertThat().body(containsString("env"));
    }

    @Test
    void testOnlyEnv() throws FileNotFoundException {
        var classLoader = Thread.currentThread().getContextClassLoader();
        var is = classLoader.getResourceAsStream("jar-last-commit-info.txt");
        PrintWriter prw = new PrintWriter("src/test/resources/jar-last-commit-info.txt");
        prw.println("These text will replace all your file content");
        prw.close();

        var request = mockRequest();
        request.assertThat().body(not(containsString("pipeline")));
        request.assertThat().body(containsString("env"));
    }

    @AfterAll
    static void fixFile() throws FileNotFoundException {
        PrintWriter prw= new PrintWriter("src/test/resources/jar-last-commit-info.txt");
        prw.println("pipeline");
        prw.close();
    }
}
