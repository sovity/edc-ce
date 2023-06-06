package de.sovity.edc.ext.wrapper.api.usecase.services;

import java.util.Optional;

import de.sovity.edc.ext.wrapper.api.common.model.AtomicConstraintDto;
import de.sovity.edc.ext.wrapper.api.common.model.ExpressionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.policy.model.XoneConstraint;

/**
 * Mapper class to convert a {@link PolicyDto} to an EDC {@link Policy}.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
public class PolicyMappingService {

    /**
     * Currently only type "USE" supported, therefore this is hardcoded.
     */
    private static final String ACTION_TYPE = "USE";

    /**
     * Converts a {@link PolicyDto} to an EDC {@link Policy}.
     *
     * @param dto The {@link PolicyDto}.
     * @return An EDC {@link Policy}
     */
    public Policy policyDtoToPolicy(PolicyDto dto) {
        if (dto == null || dto.getPermission() == null) {
            throw new IllegalArgumentException("Policy must not be null and must have a"
                    + " permission.");
        }

        return Policy.Builder.newInstance()
                .type(PolicyType.SET)
                .permission(permissionDtoToPermission(dto.getPermission()))
                .build();
    }

    private Permission permissionDtoToPermission(PermissionDto dto) {
        var builder = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance().type(ACTION_TYPE).build());
        if (dto.getConstraints() == null) {
            return builder.build();
        }

        Optional.ofNullable(expressionToConstraint(dto.getConstraints()))
                .ifPresent(builder::constraint);
        return builder.build();
    }

    private Constraint expressionToConstraint(ExpressionDto expression) {
        return switch (expression.getType()) {
            case EMPTY -> null;
            case ATOMIC_CONSTRAINT -> constraintDtoToAtomicConstraint(expression.getAtomicConstraint());
            case AND ->  {
                var builder = AndConstraint.Builder.newInstance();
                expression.getAnd().forEach(c -> builder.constraint(expressionToConstraint(c)));
                yield builder.build();
            }
            case OR -> {
                var builder = OrConstraint.Builder.newInstance();
                expression.getOr().forEach(c -> builder.constraint(expressionToConstraint(c)));
                yield builder.build();
            }
            case XOR -> {
                var builder = XoneConstraint.Builder.newInstance();
                expression.getXor().forEach(c -> builder.constraint(expressionToConstraint(c)));
                yield builder.build();
            }
        };
    }

    private Constraint constraintDtoToAtomicConstraint(AtomicConstraintDto dto) {
        return AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression(dto.getLeftExpression()))
                .rightExpression(new LiteralExpression(dto.getRightExpression()))
                .operator(Operator.valueOf(dto.getOperator().toString()))
                .build();
    }
}
