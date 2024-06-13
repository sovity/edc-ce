package de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils;

import org.junit.jupiter.api.BeforeEach;
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
