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
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.ExpressionExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.ExpressionMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.mappers.policy.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicy;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyExpression;
import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.config.ConfigUtils;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import static de.sovity.edc.utils.JsonUtils.toJson;

@RequiredArgsConstructor
public class PolicyMapper {
    private final ExpressionExtractor expressionExtractor;
    private final ExpressionMapper expressionMapper;
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final JsonLd jsonLd;
    private final Config config;

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

        var expression = expressionExtractor.getPermissionExpression(policy, errors);

        return UiPolicy.builder()
            .policyJsonLd(toJson(buildPolicyJsonLd(policy)))
            .expression(expression)
            .errors(errors.getErrors())
            .build();
    }

    /**
     * Builds an ODRL Policy from our simplified UI Policy Model.
     * <p>
     * This operation is lossless.
     *
     * @param expression policy
     * @return ODRL policy
     */
    public Policy buildPolicy(UiPolicyExpression expression) {
        var constraints = expressionMapper.buildConstraint(expression);

        var action = Action.Builder.newInstance().type(PolicyValidator.ALLOWED_ACTION).build();

        var permission = Permission.Builder.newInstance()
            .action(action)
            .constraints(constraints.stream().toList())
            .build();

        return Policy.Builder.newInstance()
            .type(PolicyType.SET)
            .permission(permission)
            .assigner(ConfigUtils.getParticipantId(config))
            .build();
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
        var expanded = jsonLd.expand(policyJsonLd)
            .orElseThrow(FailedMappingException::ofFailure);

        return typeTransformerRegistry.transform(expanded, Policy.class)
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
