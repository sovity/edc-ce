package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.AtomicConstraintDto;
import de.sovity.edc.ext.wrapper.api.common.model.ExpressionDto;
import de.sovity.edc.ext.wrapper.api.common.model.ExpressionDto.Type;
import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.XoneConstraint;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.edc.transform.spi.TypeTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Permission Transformer
 *
 * @author Haydar Qarawlus
 */
public class PermissionToPermissionDtoTransformer implements
        TypeTransformer<Permission, PermissionDto> {

    @Override
    public Class<Permission> getInputType() {
        return Permission.class;
    }

    @Override
    public Class<PermissionDto> getOutputType() {
        return PermissionDto.class;
    }

    @Override
    public @Nullable PermissionDto transform(@NotNull Permission permission,
            @NotNull TransformerContext context) {
        var builder = PermissionDto.builder();

        if (permission.getConstraints() != null && !permission.getConstraints().isEmpty()) {
            var constraint = permission.getConstraints().get(0);
            var expressionDto = transformConstraint(constraint);
            if (expressionDto == null) {
                context.problem()
                        .unexpectedType()
                        .actual(constraint.getClass())
                        .expected(AtomicConstraint.class)
                        .expected(OrConstraint.class)
                        .expected(AndConstraint.class)
                        .expected(XoneConstraint.class)
                        .report();
                return null;
            }
            builder.constraints(expressionDto);
        }

        return builder.build();
    }

    private ExpressionDto transformConstraint(Constraint constraint) {
        if (constraint instanceof AtomicConstraint atomicConstraint) {
            var leftExpression = ((LiteralExpression) atomicConstraint.getLeftExpression()).asString();
            var rightExpression = ((LiteralExpression) atomicConstraint.getRightExpression()).asString();
            var operator = atomicConstraint.getOperator();
            var operatorDto = OperatorDto.valueOf(operator.name());

            var atomicConstraintDto = new AtomicConstraintDto(leftExpression, operatorDto,
                    rightExpression);

            return new ExpressionDto(ExpressionDto.Type.ATOMIC_CONSTRAINT, atomicConstraintDto,
                    null, null, null);
        } else if (constraint instanceof AndConstraint andConstraint) {
            var expressions = andConstraint.getConstraints().stream()
                    .map(this::transformConstraint)
                    .toList();
            return new ExpressionDto(ExpressionDto.Type.AND, null, expressions, null, null);
        } else if (constraint instanceof OrConstraint orConstraint) {
            var expressions = orConstraint.getConstraints().stream()
                    .map(this::transformConstraint)
                    .toList();
            return new ExpressionDto(ExpressionDto.Type.OR, null, null, expressions, null);
        } else if (constraint instanceof XoneConstraint xoneConstraint) {
            var expressions = xoneConstraint.getConstraints().stream()
                    .map(this::transformConstraint)
                    .toList();
            return new ExpressionDto(Type.XOR, null, null, null, expressions);
        } else {
            return null; // FIXME: Set what happens in case of unknown constraint
        }
    }
}
