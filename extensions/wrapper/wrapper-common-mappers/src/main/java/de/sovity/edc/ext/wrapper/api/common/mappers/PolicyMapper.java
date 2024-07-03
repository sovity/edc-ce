package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.FailedMappingException;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.common.model.*;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.jsonld.vocab.Prop;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.policy.model.XoneConstraint;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static de.sovity.edc.utils.JsonUtils.toJson;

@RequiredArgsConstructor
public class PolicyMapper {
    private final ConstraintExtractor constraintExtractor;
    private final AtomicConstraintMapper atomicConstraintMapper;
    private final TypeTransformerRegistry typeTransformerRegistry;

    /**
     * Builds a simplified UI Policy Model from an ODRL Policy.
     * <p>
     * This operation is lossy.
     *
     * @param policy ODRL policy
     * @return ui policy
     */
    public UiPolicy buildUiPolicy(Policy policy) {
        MappingErrors errors = MappingErrors.root();

        var constraints = constraintExtractor.getPermissionConstraints(policy, errors);

        return UiPolicy.builder()
                .policyJsonLd(toJson(buildPolicyJsonLd(policy)))
                .constraints(constraints)
                .errors(errors.getErrors())
                .build();
    }

    /**
     * Builds an ODRL Policy from our simplified UI Policy Model.
     * <p>
     * This operation is lossless.
     *
     * @param policyCreateDto policy
     * @return ODRL policy
     */
    public Policy buildPolicy(UiPolicyCreateRequest policyCreateDto) {
        // UiPolicyCreateRequest is just private List<UiPolicyConstraint> constraints
        // we convert the List<UiPolicyConstraint> to a List<Constraint> using AtomicConstraintMapper
        // AtomicConstraint is an extension of Constraint
        var constraints = new ArrayList<Constraint>(atomicConstraintMapper.buildAtomicConstraints(
                policyCreateDto.getConstraints()));

        /*
        The Action object in the provided code is part of the ODRL (Open Digital Rights Language) Policy model.
        In this model, a Policy consists of one or more Permission objects, each of which has an Action and a set of Constraint objects.

        The Action represents the operation that is permitted by the Permission.
        the Action is being created with a type specified by PolicyValidator.ALLOWED_ACTION.
        This type determines what operation is allowed by the Permission.
        PolicyValidator.ALLOWED_ACTION is 'use' by default.

        ODRL includes multiple other allowed actions:
        use, play, print, copy, distribute, sell, modify, transform, delete, extract, install, access etc.
         */


        // we build an Action object with the type 'use'
        var action = Action.Builder.newInstance().type(PolicyValidator.ALLOWED_ACTION).build();

        // we build a Permission object with the action and the constraints
        var permission = Permission.Builder.newInstance()
                .action(action)
                // Permission in an extension of a Rule Class and includes
                // List<Constraint> constraints
                .constraints(constraints)
                .build();

        // we build a Policy object with the type 'set' and the permission

        /*
        SET: This corresponds to the odrl:Set policy. It represents a set of rules or permissions that need to be adhered to. For example, a SET policy might be used to define the rules for accessing a digital resource. This could include rules like "the resource can only be accessed between 9 AM and 5 PM" or "the resource can only be accessed from a specific location". An instance of this policy type must be considered by an ODRL Evaluator, which means that the evaluator must check whether these rules are being followed when access to the resource is requested.
         */

        return Policy.Builder.newInstance()
                .type(PolicyType.SET)
                .permission(permission)
                .build();
    }

    // --------------------- ERIC's PART ---------------------
    public Policy buildPolicy(List<Expression> constraintElements) {
        var constraints = buildConstraints(constraintElements);
        var action = Action.Builder.newInstance().type(Prop.Odrl.USE).build();
        var permission = Permission.Builder.newInstance()
                .action(action)
                .constraints(constraints)
                .build();

        return Policy.Builder.newInstance()
                .type(PolicyType.SET)
                .permission(permission)
                .build();
    }

    @NotNull
    private List<Constraint> buildConstraints(List<Expression> expressions) {
        return expressions.stream()
                .map(this::buildConstraint)
                .toList();
    }

    private Constraint buildConstraint(Expression expression) {
        var subExpressions = expression.getExpressions();
        return switch (expression.getExpressionType()) {
            case ATOMIC_CONSTRAINT ->
                atomicConstraintMapper.buildAtomicConstraint(expression.getAtomicConstraint());
            case AND -> AndConstraint.Builder.newInstance()
                    .constraints(buildConstraints(subExpressions))
                    .build();
            case OR -> OrConstraint.Builder.newInstance()
                    .constraints(buildConstraints(subExpressions))
                    .build();
            case XOR -> XoneConstraint.Builder.newInstance()
                    .constraints(buildConstraints(subExpressions))
                    .build();
        };
    }

    // --------------------- ERIC's PART END ---------------------

    public Policy buildMultiPolicy(MultiUiPolicyCreateRequest request) {
        var expression = request.getExpression();
        var constraints = new ArrayList<Constraint>();

        convertExpressionToConstraints(expression);

        return Policy.Builder.newInstance()
                .type(PolicyType.SET)
                .permission(Permission.Builder.newInstance()
                        .action(Action.Builder.newInstance().type(PolicyValidator.ALLOWED_ACTION).build())
                        .constraints(constraints)
                        .build())
                .build();
    }

    public List<Constraint> convertExpressionToConstraints(MultiExpression expression) {
        return switch(expression.getExpressionType()) {
            case ATOMIC_CONSTRAINT -> List.of(atomicConstraintMapper.buildAtomicConstraint(expression.getAtomicConstraint()));
            case AND -> {
                var left = convertExpressionToConstraints(expression.getLeftExpression());
                var right = convertExpressionToConstraints(expression.getRightExpression());
                yield List.of(AndConstraint.Builder.newInstance().constraints(left).constraints(right).build());
            }
            case OR -> {
                var left = convertExpressionToConstraints(expression.getLeftExpression());
                var right = convertExpressionToConstraints(expression.getRightExpression());
                yield List.of(OrConstraint.Builder.newInstance().constraints(left).constraints(right).build());
            }
            case XOR -> {
                var left = convertExpressionToConstraints(expression.getLeftExpression());
                var right = convertExpressionToConstraints(expression.getRightExpression());
                yield List.of(XoneConstraint.Builder.newInstance().constraints(left).constraints(right).build());
            }
        };
    }


    /**
     * Maps an ODRL Policy from JSON-LD to the Core EDC Type.
     * <p>
     * This operation is lossless.
     *
     * @param policyJsonLd policy JSON-LD
     * @return {@link Policy}
     */
    public Policy buildPolicy(JsonObject policyJsonLd) {
        return typeTransformerRegistry.transform(policyJsonLd, Policy.class)
                .orElseThrow(FailedMappingException::ofFailure);
    }

    /**
     * Maps an ODRL Policy from JSON-LD to the Core EDC Type.
     * <p>
     * This operation is lossless.
     *
     * @param policyJsonLd policy JSON-LD
     * @return {@link Policy}
     */
    public Policy buildPolicy(String policyJsonLd) {
        return buildPolicy(JsonUtils.parseJsonObj(policyJsonLd));
    }

    /**
     * Maps an ODRL Policy from the Core EDC Type to the JSON-LD.
     * <p>
     * This operation is lossless.
     *
     * @param policy {@link Policy}
     * @return policy JSON-LD
     */
    public JsonObject buildPolicyJsonLd(Policy policy) {
        return typeTransformerRegistry.transform(policy, JsonObject.class)
                .orElseThrow(FailedMappingException::ofFailure);
    }
}
