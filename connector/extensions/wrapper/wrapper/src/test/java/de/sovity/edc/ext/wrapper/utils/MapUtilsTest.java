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

package de.sovity.edc.ext.wrapper.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MapUtilsTest {

    @Test
    void mapValues() {
        assertThat(MapUtils.mapValues(Map.of(1, "a"), String::toUpperCase)).isEqualTo(Map.of(1, "A"));
    }

    @Test
    void associateBy() {
        assertThat(MapUtils.associateBy(List.of("a"), String::toUpperCase)).isEqualTo(Map.of("A", "a"));
    }

    @Test
    void reverse() {
        assertThat(MapUtils.reverse(Map.of("a", 1))).isEqualTo(Map.of(1, "a"));
    }
}
