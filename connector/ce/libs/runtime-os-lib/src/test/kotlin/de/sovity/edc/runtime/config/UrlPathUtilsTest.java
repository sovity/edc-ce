/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.runtime.config;

import org.junit.jupiter.api.Test;

import static de.sovity.edc.runtime.config.UrlPathUtils.urlPathJoin;
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
