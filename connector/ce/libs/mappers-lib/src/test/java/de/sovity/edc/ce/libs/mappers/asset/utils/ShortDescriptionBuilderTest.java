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
package de.sovity.edc.ce.libs.mappers.asset.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ShortDescriptionBuilderTest {
    @InjectMocks
    ShortDescriptionBuilder textUtils;

    @Test
    void test_shortDescription_null() {
        // arrange
        String text = null;

        // act
        var actual = textUtils.buildShortDescription(text);

        // assert
        assertThat(actual).isNull();
    }

    @Test
    void test_shortDescription_exceedsLength() {
        // arrange
        var text =
            "# Lorem Ipsum...\n## h2 title\n[Link text Here](example.com) 0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
        var expected =
            "Lorem Ipsum... h2 title Link text Here 012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

        // act
        var actual = textUtils.buildShortDescription(text);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void test_abbreviate_null() {
        // arrange
        String text = null;

        // act
        var actual = textUtils.abbreviate(text, 1);

        // assert
        assertThat(actual).isNull();
    }

    @Test
    void test_abbreviate_emptyString() {
        // arrange
        var text = "";

        // act
        var actual = textUtils.abbreviate(text, 1);

        // assert
        assertThat(actual).isEqualTo("");
    }

    @Test
    void test_abbreviate_lengthLessThanMaxCharacters() {
        // arrange
        var text = "a";

        // act
        var actual = textUtils.abbreviate(text, 2);

        // assert
        assertThat(actual).isEqualTo("a");
    }

    @Test
    void test_abbreviate_lengthLongerThanMaxCharacters() {
        // arrange
        var text = "aa";

        // act
        var actual = textUtils.abbreviate(text, 1);

        // assert
        assertThat(actual).isEqualTo("a");
    }
}
