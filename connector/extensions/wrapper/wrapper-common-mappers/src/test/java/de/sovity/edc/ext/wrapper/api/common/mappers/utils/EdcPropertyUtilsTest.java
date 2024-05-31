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

package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EdcPropertyUtilsTest {
    EdcPropertyUtils edcPropertyUtils;

    @BeforeEach
    void setup() {
        edcPropertyUtils = new EdcPropertyUtils();
    }

    @Test
    void testToObjectMap() {
        assertThat(edcPropertyUtils.toMapOfObject(Map.of(
                "a", "b"
        ))).isEqualTo(Map.of(
                "a", "b"
        ));
    }

    @Test
    void testToStringMap() {
        // arrange
        Map<String, Object> map = new HashMap<>();
        map.put("a", "b");
        map.put("c", 1);
        map.put("d", 2.0);
        map.put("e", null);
        map.put("f", new HashMap<>());

        // act
        var actual = edcPropertyUtils.truncateToMapOfString(map);

        // assert
        Map<String, String> expected = new HashMap<>();
        expected.put("a", "b");
        expected.put("c", "1");
        expected.put("d", "2.0");
        expected.put("e", null);

        assertThat(actual).isEqualTo(expected);
    }
}
