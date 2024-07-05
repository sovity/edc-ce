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

package de.sovity.edc.ext.catalog.crawler.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class CollectionUtils2Test {
    @Test
    void difference() {
        assertThat(CollectionUtils2.difference(List.of(1, 2, 3), List.of(2, 3, 4))).containsExactly(1);
    }

    @Test
    void isNotEmpty_withEmptyList() {
        assertThat(CollectionUtils2.isNotEmpty(List.of())).isFalse();
    }

    @Test
    void isNotEmpty_withNull() {
        assertThat(CollectionUtils2.isNotEmpty(null)).isFalse();
    }

    @Test
    void isNotEmpty_withNonEmptyList() {
        assertThat(CollectionUtils2.isNotEmpty(List.of(1))).isTrue();
    }
}
