/*
 * Copyright (c) 2024 sovity GmbH
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

package de.sovity.edc.utils.config.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static de.sovity.edc.utils.config.utils.UrlPathUtils.urlPathJoin;

class UrlPathUtilsTest {
    @Test
    void urlPathJoin_empty() {
        Assertions.assertThat(urlPathJoin()).isEmpty();
        Assertions.assertThat(urlPathJoin("")).isEmpty();
        Assertions.assertThat(urlPathJoin("/")).isEqualTo("/");
    }

    @Test
    void urlPathJoin_relative() {
        Assertions.assertThat(urlPathJoin("a")).isEqualTo("a");
        Assertions.assertThat(urlPathJoin("a/")).isEqualTo("a/");
        Assertions.assertThat(urlPathJoin("a", "b")).isEqualTo("a/b");
        Assertions.assertThat(urlPathJoin("a/", "b")).isEqualTo("a/b");
        Assertions.assertThat(urlPathJoin("a", "/b")).isEqualTo("a/b");
        Assertions.assertThat(urlPathJoin("a/", "/b")).isEqualTo("a/b");
    }

    @Test
    void urlPathJoin_absolute() {
        Assertions.assertThat(urlPathJoin("/a")).isEqualTo("/a");
        Assertions.assertThat(urlPathJoin("/a/")).isEqualTo("/a/");
        Assertions.assertThat(urlPathJoin("/a", "b")).isEqualTo("/a/b");
        Assertions.assertThat(urlPathJoin("/a/", "b")).isEqualTo("/a/b");
        Assertions.assertThat(urlPathJoin("/a", "/b")).isEqualTo("/a/b");
        Assertions.assertThat(urlPathJoin("/a/", "/b")).isEqualTo("/a/b");
    }

    @Test
    void urlPathJoin_immediate_protocol() {
        Assertions.assertThat(urlPathJoin("https://")).isEqualTo("https://");
        Assertions.assertThat(urlPathJoin("https://", "b")).isEqualTo("https://b");
        Assertions.assertThat(urlPathJoin("https://", "/b")).isEqualTo("https://b");
    }

    @Test
    void urlPathJoin_protocol() {
        Assertions.assertThat(urlPathJoin("https://a")).isEqualTo("https://a");
        Assertions.assertThat(urlPathJoin("https://a/")).isEqualTo("https://a/");
        Assertions.assertThat(urlPathJoin("https://a", "b")).isEqualTo("https://a/b");
        Assertions.assertThat(urlPathJoin("https://a/", "b")).isEqualTo("https://a/b");
        Assertions.assertThat(urlPathJoin("https://a", "/b")).isEqualTo("https://a/b");
        Assertions.assertThat(urlPathJoin("https://a/", "/b")).isEqualTo("https://a/b");
    }

    @Test
    void urlPathJoin_protocol_overruling_not_enabled() {
        Assertions.assertThat(urlPathJoin("https://ignored", "https://a")).isEqualTo("https://ignored/https://a");
        Assertions.assertThat(urlPathJoin("https://ignored", "https://a/")).isEqualTo("https://ignored/https://a/");
        Assertions.assertThat(urlPathJoin("https://ignored", "https://a", "b")).isEqualTo("https://ignored/https://a/b");
        Assertions.assertThat(urlPathJoin("https://ignored", "https://a/", "b")).isEqualTo("https://ignored/https://a/b");
        Assertions.assertThat(urlPathJoin("https://ignored", "https://a", "/b")).isEqualTo("https://ignored/https://a/b");
        Assertions.assertThat(urlPathJoin("https://ignored", "https://a/", "/b")).isEqualTo("https://ignored/https://a/b");
    }
}
