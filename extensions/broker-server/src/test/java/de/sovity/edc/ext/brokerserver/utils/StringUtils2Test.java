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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtils2Test {
    @Test
    void removeSuffix_emptyStrings() {
        assertThat(StringUtils2.removeSuffix("", "")).isEmpty();
    }

    @Test
    void removeSuffix_emptySuffix() {
        assertThat(StringUtils2.removeSuffix("test", "")).isEqualTo("test");
    }


    @Test
    void removeSuffix_withSuffix() {
        assertThat(StringUtils2.removeSuffix("testabc", "abc")).isEqualTo("test");
    }


    @Test
    void removeSuffix_withoutSuffix() {
        assertThat(StringUtils2.removeSuffix("test", "abc")).isEqualTo("test");
    }


    @Test
    void lowercaseWords_emptyString() {
        assertThat(StringUtils2.lowercaseWords("")).isEmpty();
    }

    @Test
    void lowercaseWords_blankString() {
        assertThat(StringUtils2.lowercaseWords(" ")).isEmpty();
    }


    @Test
    void lowercaseWords_someWords() {
        assertThat(StringUtils2.lowercaseWords(" a  \n\t B  a ")).isEqualTo(List.of("a", "b", "a"));
    }
}
