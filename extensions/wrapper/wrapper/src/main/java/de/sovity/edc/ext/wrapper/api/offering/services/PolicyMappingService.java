package de.sovity.edc.ext.wrapper.api.offering.services;

import java.util.ArrayList;
import java.util.List;

import de.sovity.edc.ext.wrapper.api.common.model.ConstraintDto;
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

/**
 * Mapper class to convert a {@link PolicyDto} to an EDC {@link Policy}.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
public class PolicyMappingService {


    /**
     * Converts a {@link PolicyDto} to an EDC {@link Policy}.
     *
     * @param dto The {@link PolicyDto}.
     * @return An EDC {@link Policy}
     */
    public Policy policyDtoToPolicy(PolicyDto dto) {
        return Policy.Builder.newInstance()
                .type(PolicyType.valueOf(dto.getType().getType().toUpperCase()))
                .permission(constraintsToPermission(dto))
                .build();
    }

    private AtomicConstraint constraintDtoToAtomicConstraint(ConstraintDto dto) {
        return AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression(dto.getLeftExpression()))
                .rightExpression(new LiteralExpression(dto.getRightExpression()))
                .operator(Operator.valueOf(dto.getOperatorDto().toString()))
                .build();
    }

    private Permission constraintsToPermission(PolicyDto dto) {
        var constraints = new ArrayList<Constraint>();

        if (dto.getPermission().getConstraintDto() != null) {
            constraints.add(
                    constraintDtoToAtomicConstraint(dto.getPermission().getConstraintDto()));

        }

        if (dto.getPermission().getAndConstraintDtos() != null) {
            var andConstraints = new ArrayList<Constraint>();
            for (ConstraintDto dtoConstraint : dto.getPermission().getAndConstraintDtos()) {
                andConstraints.add(constraintDtoToAtomicConstraint(dtoConstraint));
            }
            var andConstraint = AndConstraint.Builder.newInstance()
                    .constraints(andConstraints)
                    .build();
            constraints.add(andConstraint);
        }

        if (dto.getPermission().getOrConstraintDtos() != null) {
            var orConstraints = new ArrayList<Constraint>();
            for (ConstraintDto dtoConstraint : dto.getPermission().getOrConstraintDtos()) {
                orConstraints.add(constraintDtoToAtomicConstraint(dtoConstraint));
            }
            var orConstraint = OrConstraint.Builder.newInstance()
                    .constraints(orConstraints)
                    .build();
            constraints.add(orConstraint);
        }

        return Permission.Builder.newInstance()
                .constraints(constraints)
                .action(Action.Builder.newInstance().type(dto.getPermission().getAction()).build())
                .build();
    }
}
