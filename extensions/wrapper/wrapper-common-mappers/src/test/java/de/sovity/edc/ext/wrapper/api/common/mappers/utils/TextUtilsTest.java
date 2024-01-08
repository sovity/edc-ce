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
        var actual = textUtils.abbreviate(text, 300);

        // assert
        assertThat(actual).isEqualTo(null);
    }

    @Test
    void test_abbreviate_emptyString() {
        // arrange
        String text = "";

        // act
        var actual = textUtils.abbreviate(text, 300);

        // assert
        assertThat(actual).isEqualTo("");
    }

    @Test
    void test_abbreviate_lengthLessThanMaxCharacters() {
        // arrange
        String text = "Hello";

        // act
        var actual = textUtils.abbreviate(text, 300);

        // assert
        assertThat(actual).isEqualTo("Hello");
    }

    @Test
    void test_abbreviate_lengthLongerThanMaxCharacters() {
        // arrange
        var tmp = "0123456789";
        var text = tmp.repeat(40); // 400 length String
        var expected = tmp.repeat(30);

        // act
        var actual = textUtils.abbreviate(text, 300);

        // assert
        assertThat(actual).isEqualTo(expected);
    }
}
