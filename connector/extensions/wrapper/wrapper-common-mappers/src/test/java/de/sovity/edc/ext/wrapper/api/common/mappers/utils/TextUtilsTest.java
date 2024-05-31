package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextUtilsTest {
    TextUtils textUtils;

    @BeforeEach
    void setup() {
        textUtils = new TextUtils();
    }

    @Test
    void test_abbreviate_null() {
        // arrange
        String text = null;

        // act
        var actual = textUtils.abbreviate(text, 1);

        // assert
        assertThat(actual).isEqualTo(null);
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
