package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.AtomicConstraintDto;
import de.sovity.edc.ext.wrapper.api.common.model.ExpressionDto;
import de.sovity.edc.ext.wrapper.api.common.model.OperatorDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import org.eclipse.edc.policy.model.*;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.edc.transform.spi.TypeTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Permission Transformer
 *
 * @author Haydar Qarawlus
 */
public class PermissionTransformer implements TypeTransformer<Permission, PermissionDto> {
    @Override
    public Class<Permission> getInputType() {
        return Permission.class;
    }

    @Override
    public Class<PermissionDto> getOutputType() {
        return PermissionDto.class;
    }

    @Override
    public @Nullable PermissionDto transform(@NotNull Permission permission, @NotNull TransformerContext context) {
        var permissionDtoBuilder = PermissionDto.builder();

        if (permission.getConstraints() != null && !permission.getConstraints().isEmpty()) {
            var constraint = permission.getConstraints().get(0);
            var expressionDto = transformConstraint(constraint);
            permissionDtoBuilder.constraints(expressionDto);
            return permissionDtoBuilder.build();

        }
        return null;



    }

    private ExpressionDto transformConstraint(Constraint constraint) {

        if (constraint instanceof AtomicConstraint) {
            var atomicConstraint = (AtomicConstraint) constraint;
            var leftExpression = ((LiteralExpression)atomicConstraint.getLeftExpression()).asString();
            var rightExpression = ((LiteralExpression)atomicConstraint.getRightExpression()).asString();
            var operator = atomicConstraint.getOperator();
            var operatorDto = OperatorDto.valueOf(operator.name());

            var atomicConstraintDto = new AtomicConstraintDto(leftExpression,operatorDto, rightExpression);

            return new ExpressionDto(ExpressionDto.Type.ATOMIC_CONSTRAINT, atomicConstraintDto,
                    null, null, null);
        } else if (constraint instanceof AndConstraint) {
            var constraintList = new ArrayList<ExpressionDto>();

            var andConstraint = (AndConstraint) constraint;
            andConstraint.getConstraints().forEach(constraintElement -> {
                constraintList.add(transformConstraint(constraintElement));
            });

            return new ExpressionDto(ExpressionDto.Type.AND, null, constraintList, null, null);

        } else if (constraint instanceof OrConstraint) {
            var constraintList = new ArrayList<ExpressionDto>();

            var orConstraint = (OrConstraint) constraint;
            orConstraint.getConstraints().forEach(constraintElement -> {
                constraintList.add(transformConstraint(constraintElement));
            });

            return new ExpressionDto(ExpressionDto.Type.AND, null, constraintList, null, null);

        } else if (constraint instanceof XoneConstraint) {
            var constraintList = new ArrayList<ExpressionDto>();

            var xoneConstraint = (XoneConstraint) constraint;
            xoneConstraint.getConstraints().forEach(constraintElement -> {
                constraintList.add(transformConstraint(constraintElement));
            });

            return new ExpressionDto(ExpressionDto.Type.AND, null, constraintList, null, null);

        } else
            return null; // FIXME: Set what happens in case of unknown constraint
    }
}
