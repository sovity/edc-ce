package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteral;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteralType;
import org.eclipse.edc.policy.model.Expression;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LiteralMapperTest {
    LiteralMapper literalMapper;

    @BeforeEach
    void setup() {
        literalMapper = new LiteralMapper(new ObjectMapper());
    }

    @Test
    void test_getUiLiteralValue_string() {
        // arrange
        var literal = UiPolicyLiteral.ofString("test");

        // act
        var actual = literalMapper.getUiLiteralValue(literal);

        // assert
        assertThat(actual).isEqualTo("test");
    }

    @Test
    void test_getUiLiteralValue_stringNull() {
        // arrange
        var literal = UiPolicyLiteral.ofString(null);

        // act
        var actual = literalMapper.getUiLiteralValue(literal);

        // assert
        assertThat(actual).isNull();
    }

    @Test
    void test_getUiLiteralValue_list() {
        // arrange
        var literal = UiPolicyLiteral.ofStringList(List.of("test"));

        // act
        var actual = literalMapper.getUiLiteralValue(literal);

        // assert
        assertThat(actual).isEqualTo(List.of("test"));
    }

    @Test
    void test_getUiLiteralValue_json() {
        // arrange
        var literal = UiPolicyLiteral.ofJson("true");

        // act
        var actual = literalMapper.getUiLiteralValue(literal);

        // assert
        assertThat(actual).isEqualTo(true);
    }

    @Test
    void test_getLiteralExpression_null() {
        // arrange
        var expression = (Expression) null;
        var errors = MappingErrors.root();

        // act
        var actual = literalMapper.getLiteralExpression(expression, errors);

        // assert
        assertThat(actual).isEmpty();
        assertThat(errors.getErrors()).containsExactly("$: Expression is null.");
    }

    @Test
    void test_getLiteralExpression_otherSubclass() {
        // arrange
        var expression = mock(Expression.class);
        var errors = MappingErrors.root();

        // act
        var actual = literalMapper.getLiteralExpression(expression, errors);

        // assert
        assertThat(actual).isEmpty();
        assertThat(errors.getErrors()).hasSize(1);
        assertThat(errors.getErrors().get(0)).startsWith("$: Expression type is not LiteralExpression, but ");
    }

    @Test
    void test_getLiteralExpression_ok() {
        // arrange
        var expression = mock(LiteralExpression.class);
        var errors = MappingErrors.root();

        // act
        var actual = literalMapper.getLiteralExpression(expression, errors);

        // assert
        assertThat(actual).contains(expression);
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void test_getExpressionString_null() {
        // arrange
        var expression = mock(LiteralExpression.class);
        var errors = MappingErrors.root();

        when(expression.getValue()).thenReturn(null);

        // act
        var actual = literalMapper.getExpressionString(expression, errors);

        // assert
        assertThat(actual).isEmpty();
        assertThat(errors.getErrors()).containsExactly("$.value: Is not a string, but null.");
    }

    @Test
    void test_getExpressionString_ok() {
        // arrange
        var expression = mock(LiteralExpression.class);
        var errors = MappingErrors.root();

        when(expression.getValue()).thenReturn("test");

        // act
        var actual = literalMapper.getExpressionString(expression, errors);

        // assert
        assertThat(actual).contains("test");
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void test_getExpressionString_notAString() {
        // arrange
        var expression = mock(LiteralExpression.class);
        var errors = MappingErrors.root();

        when(expression.getValue()).thenReturn(5);

        // act
        var actual = literalMapper.getExpressionString(expression, errors);

        // assert
        assertThat(actual).isEmpty();
        assertThat(errors.getErrors()).containsExactly("$.value: Is not a string.");
    }

    @Test
    void test_getExpressionValue_null() {
        // arrange
        var expression = mock(LiteralExpression.class);
        var errors = MappingErrors.root();

        when(expression.getValue()).thenReturn(null);

        // act
        var actual = literalMapper.getExpressionValue(expression, errors);

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(actual.get().getType()).isEqualTo(UiPolicyLiteralType.JSON);
        assertThat(actual.get().getValue()).isEqualTo("null");
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void test_getExpressionValue_string() {
        // arrange
        var expression = mock(LiteralExpression.class);
        var errors = MappingErrors.root();

        when(expression.getValue()).thenReturn("test");

        // act
        var actual = literalMapper.getExpressionValue(expression, errors);

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(actual.get().getType()).isEqualTo(UiPolicyLiteralType.STRING);
        assertThat(actual.get().getValue()).isEqualTo("test");
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void test_getExpressionValue_string_list() {
        // arrange
        var expression = mock(LiteralExpression.class);
        var errors = MappingErrors.root();

        when(expression.getValue()).thenReturn(Arrays.asList(null, "test"));

        // act
        var actual = literalMapper.getExpressionValue(expression, errors);

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(actual.get().getType()).isEqualTo(UiPolicyLiteralType.STRING_LIST);
        assertThat(actual.get().getValueList()).isEqualTo(Arrays.asList(null, "test"));
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void test_getExpressionValue_string_json() {
        // arrange
        var expression = mock(LiteralExpression.class);
        var errors = MappingErrors.root();

        when(expression.getValue()).thenReturn(true);

        // act
        var actual = literalMapper.getExpressionValue(expression, errors);

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(actual.get().getType()).isEqualTo(UiPolicyLiteralType.JSON);
        assertThat(actual.get().getValue()).isEqualTo("true");
        assertThat(errors.getErrors()).isEmpty();
    }

    @Test
    void test_getExpressionValue_other_list() {
        // arrange
        var expression = mock(LiteralExpression.class);
        var errors = MappingErrors.root();

        when(expression.getValue()).thenReturn(Arrays.asList("string", 5));

        // act
        var actual = literalMapper.getExpressionValue(expression, errors);

        // assert
        assertThat(actual).isNotEmpty();
        assertThat(actual.get().getType()).isEqualTo(UiPolicyLiteralType.JSON);
        assertThat(actual.get().getValue()).isEqualTo("[\"string\",5]");
        assertThat(errors.getErrors()).isEmpty();
    }
}
