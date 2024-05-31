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

class JsonUtils2Test {
    @Test
    void equalityTests() {
        assertTrue(JsonUtils2.isEqualJson(null, null));
        assertTrue(JsonUtils2.isEqualJson("null", "null"));
        assertTrue(JsonUtils2.isEqualJson("{}", "{}"));
        assertTrue(JsonUtils2.isEqualJson("{\"a\": true, \"b\": \"hello\"}", "{\"a\": true,\"b\": \"hello\"}"));
        assertTrue(JsonUtils2.isEqualJson("{\"a\": true, \"b\": \"hello\"}", "{\"b\": \"hello\", \"a\": true}"));

        assertFalse(JsonUtils2.isEqualJson(null, "1"));
        assertFalse(JsonUtils2.isEqualJson("1", null));
    }
}
