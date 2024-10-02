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
 *       sovity GmbH - init
 *
 */

import org.junit.jupiter.api.Test;

import static de.sovity.edc.utils.config.utils.UrlPathUtils.urlPathJoin;
import static org.assertj.core.api.Assertions.assertThat;

class UrlPathUtilsTest {
    @Test
    void urlPathJoin_empty() {
        assertThat(urlPathJoin()).isEmpty();
        assertThat(urlPathJoin("")).isEmpty();
        assertThat(urlPathJoin("/")).isEqualTo("/");
    }

    @Test
    void urlPathJoin_relative() {
        assertThat(urlPathJoin("a")).isEqualTo("a");
        assertThat(urlPathJoin("a/")).isEqualTo("a/");
        assertThat(urlPathJoin("a", "b")).isEqualTo("a/b");
        assertThat(urlPathJoin("a/", "b")).isEqualTo("a/b");
        assertThat(urlPathJoin("a", "/b")).isEqualTo("a/b");
        assertThat(urlPathJoin("a/", "/b")).isEqualTo("a/b");
    }

    @Test
    void urlPathJoin_absolute() {
        assertThat(urlPathJoin("/a")).isEqualTo("/a");
        assertThat(urlPathJoin("/a/")).isEqualTo("/a/");
        assertThat(urlPathJoin("/a", "b")).isEqualTo("/a/b");
        assertThat(urlPathJoin("/a/", "b")).isEqualTo("/a/b");
        assertThat(urlPathJoin("/a", "/b")).isEqualTo("/a/b");
        assertThat(urlPathJoin("/a/", "/b")).isEqualTo("/a/b");
    }

    @Test
    void urlPathJoin_immediate_protocol() {
        assertThat(urlPathJoin("https://")).isEqualTo("https://");
        assertThat(urlPathJoin("https://", "b")).isEqualTo("https://b");
        assertThat(urlPathJoin("https://", "/b")).isEqualTo("https://b");
    }

    @Test
    void urlPathJoin_protocol() {
        assertThat(urlPathJoin("https://a")).isEqualTo("https://a");
        assertThat(urlPathJoin("https://a/")).isEqualTo("https://a/");
        assertThat(urlPathJoin("https://a", "b")).isEqualTo("https://a/b");
        assertThat(urlPathJoin("https://a/", "b")).isEqualTo("https://a/b");
        assertThat(urlPathJoin("https://a", "/b")).isEqualTo("https://a/b");
        assertThat(urlPathJoin("https://a/", "/b")).isEqualTo("https://a/b");
    }

    @Test
    void urlPathJoin_protocol_overruling_not_enabled() {
        assertThat(urlPathJoin("https://ignored", "https://a")).isEqualTo("https://ignored/https://a");
        assertThat(urlPathJoin("https://ignored", "https://a/")).isEqualTo("https://ignored/https://a/");
        assertThat(urlPathJoin("https://ignored", "https://a", "b")).isEqualTo("https://ignored/https://a/b");
        assertThat(urlPathJoin("https://ignored", "https://a/", "b")).isEqualTo("https://ignored/https://a/b");
        assertThat(urlPathJoin("https://ignored", "https://a", "/b")).isEqualTo("https://ignored/https://a/b");
        assertThat(urlPathJoin("https://ignored", "https://a/", "/b")).isEqualTo("https://ignored/https://a/b");
    }
}
