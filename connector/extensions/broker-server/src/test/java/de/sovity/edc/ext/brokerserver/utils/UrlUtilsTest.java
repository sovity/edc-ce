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

package de.sovity.edc.ext.brokerserver.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlUtilsTest {
    @Test
    void test_urlUtils() {
        assertTrue(UrlUtils.isValidUrl("http://localhost:8080"));
        assertTrue(UrlUtils.isValidUrl(" http://localhost:8080"));

        assertFalse(UrlUtils.isValidUrl("test"));
        assertFalse(UrlUtils.isValidUrl(""));
        assertFalse(UrlUtils.isValidUrl(" "));
        assertFalse(UrlUtils.isValidUrl(null));
    }
}
