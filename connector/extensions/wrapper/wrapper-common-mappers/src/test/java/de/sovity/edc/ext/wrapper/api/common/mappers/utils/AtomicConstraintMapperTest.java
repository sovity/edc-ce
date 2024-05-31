package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.OperatorMapper;
import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteral;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyLiteralType;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Expression;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class AtomicConstraintMapperTest {

    @Test
    void test_buildAtomicConstraint_null() {
        // arrange
        var atomicConstraintMapper = newAtomicConstraintMapper();

        // act
        var actual = atomicConstraintMapper.buildAtomicConstraints(null);

        // assert
        assertThat(actual).isEmpty();
    }

    @Test
    void test_buildAtomicConstraint() {
        // arrange
        var literalMapper = mock(LiteralMapper.class);
        var atomicConstraintMapper = newAtomicConstraintMapper(literalMapper);

        var right = mock(UiPolicyLiteral.class);
        var constraint = new UiPolicyConstraint("left", OperatorDto.EQ, right);

        when(literalMapper.getUiLiteralValue(right)).thenReturn("right");

        // act
        var actual = atomicConstraintMapper.buildAtomicConstraints(List.of(constraint));

        // assert
        assertThat(actual).hasSize(1);
        var atomicConstraint = actual.get(0);
        assertThat(atomicConstraint.getLeftExpression()).isInstanceOfSatisfying(LiteralExpression.class, literalExpression ->
                assertThat(literalExpression.getValue()).isEqualTo("left"));
        assertThat(atomicConstraint.getRightExpression()).isInstanceOfSatisfying(LiteralExpression.class, literalExpression ->
                assertThat(literalExpression.getValue()).isEqualTo("right"));
        assertThat(atomicConstraint.getOperator()).isEqualTo(Operator.EQ);
    }

    @Test
    void test_buildUiConstraint_string() {
        // arrange
        var atomicConstraintMapper = newAtomicConstraintMapper();
        var errors = MappingErrors.root();
        var atomicConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression("left"))
                .operator(Operator.EQ)
                .rightExpression(new LiteralExpression("right"))
                .build();

        // act
        var actual = atomicConstraintMapper.buildUiConstraint(atomicConstraint, errors);

        // assert
        assertThat(errors.getErrors()).isEmpty();
        assertThat(actual).isPresent();
        assertThat(actual.get().getLeft()).isEqualTo("left");
        assertThat(actual.get().getOperator()).isEqualTo(OperatorDto.EQ);
        assertThat(actual.get().getRight().getType()).isEqualTo(UiPolicyLiteralType.STRING);
        assertThat(actual.get().getRight().getValue()).isEqualTo("right");
    }

    @Test
    void test_buildUiConstraint_stringList() {
        // arrange
        var atomicConstraintMapper = newAtomicConstraintMapper();
        var errors = MappingErrors.root();
        var atomicConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression("left"))
                .operator(Operator.EQ)
                .rightExpression(new LiteralExpression(List.of("right")))
                .build();

        // act
        var actual = atomicConstraintMapper.buildUiConstraint(atomicConstraint, errors);

        // assert
        assertThat(errors.getErrors()).isEmpty();
        assertThat(actual).isPresent();
        assertThat(actual.get().getLeft()).isEqualTo("left");
        assertThat(actual.get().getOperator()).isEqualTo(OperatorDto.EQ);
        assertThat(actual.get().getRight().getType()).isEqualTo(UiPolicyLiteralType.STRING_LIST);
        assertThat(actual.get().getRight().getValueList()).isEqualTo(List.of("right"));
    }

    @Test
    void test_buildUiConstraint_json() {
        // arrange
        var atomicConstraintMapper = newAtomicConstraintMapper();
        var errors = MappingErrors.root();
        var atomicConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression("left"))
                .operator(Operator.EQ)
                .rightExpression(new LiteralExpression(Map.of("a", "b")))
                .build();

        // act
        var actual = atomicConstraintMapper.buildUiConstraint(atomicConstraint, errors);

        // assert
        assertThat(errors.getErrors()).isEmpty();
        assertThat(actual).isPresent();
        assertThat(actual.get().getLeft()).isEqualTo("left");
        assertThat(actual.get().getOperator()).isEqualTo(OperatorDto.EQ);
        assertThat(actual.get().getRight().getType()).isEqualTo(UiPolicyLiteralType.JSON);
        assertThat(actual.get().getRight().getValue()).isEqualTo("{\"a\":\"b\"}");
    }

    @Test
    void test_buildUiConstraint_string2() {
        // arrange
        var atomicConstraintMapper = newAtomicConstraintMapper();
        var errors = MappingErrors.root();
        var atomicConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression("left"))
                .operator(Operator.EQ)
                .rightExpression(new LiteralExpression("right"))
                .build();

        // act
        var actual = atomicConstraintMapper.buildUiConstraint(atomicConstraint, errors);

        // assert
        assertThat(errors.getErrors()).isEmpty();
        assertThat(actual).isPresent();
        assertThat(actual.get().getLeft()).isEqualTo("left");
        assertThat(actual.get().getOperator()).isEqualTo(OperatorDto.EQ);
        assertThat(actual.get().getRight().getType()).isEqualTo(UiPolicyLiteralType.STRING);
        assertThat(actual.get().getRight().getValue()).isEqualTo("right");
    }

    @Test
    void test_buildUiConstraint_leftBad() {
        // arrange
        var literalMapper = mock(LiteralMapper.class);
        var atomicConstraintMapper = newAtomicConstraintMapper(literalMapper);
        var errors = MappingErrors.root();

        var leftExpression = mock(Expression.class);
        var rightExpression = mock(Expression.class);
        var atomicConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(leftExpression)
                .operator(Operator.EQ)
                .rightExpression(rightExpression)
                .build();

        when(literalMapper.getExpressionString(same(leftExpression), any())).thenAnswer(i -> {
            i.getArgument(1, MappingErrors.class).add("my-error");
            return Optional.empty();
        });
        when(literalMapper.getExpressionValue(same(rightExpression), any())).thenReturn(Optional.of(mock(UiPolicyLiteral.class)));

        // act
        var actual = atomicConstraintMapper.buildUiConstraint(atomicConstraint, errors);

        // assert
        assertThat(actual).isEmpty();
        assertThat(errors.getErrors()).containsExactly("$.leftExpression: my-error");
    }

    @Test
    void test_buildUiConstraint_rightBad() {
        // arrange
        var literalMapper = mock(LiteralMapper.class);
        var atomicConstraintMapper = newAtomicConstraintMapper(literalMapper);
        var errors = MappingErrors.root();

        var leftExpression = mock(Expression.class);
        var rightExpression = mock(Expression.class);
        var atomicConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(leftExpression)
                .operator(Operator.EQ)
                .rightExpression(rightExpression)
                .build();

        when(literalMapper.getExpressionString(same(leftExpression), any())).thenReturn(Optional.of("left"));
        when(literalMapper.getExpressionValue(same(rightExpression), any())).thenAnswer(i -> {
            i.getArgument(1, MappingErrors.class).add("my-error");
            return Optional.empty();
        });

        // act
        var actual = atomicConstraintMapper.buildUiConstraint(atomicConstraint, errors);

        // assert
        assertThat(actual).isEmpty();
        assertThat(errors.getErrors()).containsExactly("$.rightExpression: my-error");
    }

    @Test
    void test_buildUiConstraint_operatorBad() {
        // arrange
        var atomicConstraintMapper = newAtomicConstraintMapper();
        var errors = MappingErrors.root();
        var atomicConstraint = AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression("left"))
                .operator(null)
                .rightExpression(new LiteralExpression("right"))
                .build();

        // act
        var actual = atomicConstraintMapper.buildUiConstraint(atomicConstraint, errors);

        // assert
        assertThat(actual).isEmpty();
        assertThat(errors.getErrors()).containsExactly("$.operator: Operator is null.");
    }

    private AtomicConstraintMapper newAtomicConstraintMapper() {
        return newAtomicConstraintMapper(new LiteralMapper(new ObjectMapper()));
    }

    private AtomicConstraintMapper newAtomicConstraintMapper(LiteralMapper literalMapper) {
        return new AtomicConstraintMapper(literalMapper, new OperatorMapper());
    }
}
