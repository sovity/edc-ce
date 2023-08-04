package de.sovity.edc.ext.wrapper.api.common.mappers.utils;

import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.LiteralExpression;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AtomicConstraintMapper {
    private final LiteralMapper literalMapper;

    /**
     * Create ODRL {@link AtomicConstraint}s from {@link UiPolicyConstraint}s
     * <p>
     * This operation is lossless.
     *
     * @param constraints ui constraints
     * @return ODRL constraints
     */
    public List<AtomicConstraint> buildAtomicConstraints(List<UiPolicyConstraint> constraints) {
        if (constraints == null) {
            return List.of();
        }

        return constraints.stream()
                .map(this::buildAtomicConstraint)
                .toList();
    }

    /**
     * Create {@link UiPolicyConstraint} from ODRL {@link AtomicConstraint}
     * <p>
     * This operation is lossy.
     *
     * @param atomicConstraint atomic contraints
     * @param errors           errors
     * @return ui policy constraint
     */
    public Optional<UiPolicyConstraint> buildUiConstraint(
            @NonNull AtomicConstraint atomicConstraint,
            MappingErrors errors
    ) {
        var leftValue = literalMapper.getExpressionString(atomicConstraint.getLeftExpression(),
                errors.forChildObject("leftExpression"));

        var operator = atomicConstraint.getOperator();
        if (operator == null) {
            errors.forChildObject("operator").add("Operator is null.");
        }

        var rightValue = literalMapper.getExpressionValue(atomicConstraint.getRightExpression(),
                errors.forChildObject("rightExpression"));

        if (leftValue.isEmpty() || rightValue.isEmpty() || operator == null) {
            return Optional.empty();
        }

        UiPolicyConstraint result = UiPolicyConstraint.builder()
                .left(leftValue.get())
                .operator(operator)
                .right(rightValue.get())
                .build();

        return Optional.of(result);
    }

    private AtomicConstraint buildAtomicConstraint(UiPolicyConstraint constraint) {
        var left = constraint.getLeft();
        var operator = constraint.getOperator();
        var right = literalMapper.getUiLiteralValue(constraint.getRight());

        return AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression(left))
                .operator(operator)
                .rightExpression(new LiteralExpression(right))
                .build();
    }
}
