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

package de.sovity.edc.extension.jwks.util;

import de.sovity.edc.extension.jwks.controller.EdcJwksControllerTest;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Scanner;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TestCertFromFileUtil {

    public static String getCertStringFromFile() throws IOException {
        var classLoader = EdcJwksControllerTest.class.getClassLoader();
        try (var inputStream = classLoader.getResourceAsStream("cert.pem")) {
            try (var scanner = new Scanner(inputStream).useDelimiter("\\A")) {
                return scanner.hasNext() ? scanner.next() : "";
            }
        }
    }

}
