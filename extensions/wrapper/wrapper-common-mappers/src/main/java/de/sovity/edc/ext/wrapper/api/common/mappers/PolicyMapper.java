/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.asset.utils.FailedMappingException;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.common.model.Expression;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicy;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
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
        var constraints = new ArrayList<Constraint>(atomicConstraintMapper.buildAtomicConstraints(
                policyCreateDto.getConstraints()));

        var action = Action.Builder.newInstance().type(PolicyValidator.ALLOWED_ACTION).build();

        var permission = Permission.Builder.newInstance()
                .action(action)
                .constraints(constraints)
                .build();

        return Policy.Builder.newInstance()
                .type(PolicyType.SET)
                .permission(permission)
                .build();
    }

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
