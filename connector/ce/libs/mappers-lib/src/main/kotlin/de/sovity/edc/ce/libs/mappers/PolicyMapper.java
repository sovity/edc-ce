/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.ce.libs.mappers;

import de.sovity.edc.ce.api.common.model.UiPolicy;
import de.sovity.edc.ce.api.common.model.UiPolicyExpression;
import de.sovity.edc.ce.libs.mappers.asset.utils.FailedMappingException;
import de.sovity.edc.ce.libs.mappers.policy.ExpressionExtractor;
import de.sovity.edc.ce.libs.mappers.policy.ExpressionMapper;
import de.sovity.edc.ce.libs.mappers.policy.MappingErrors;
import de.sovity.edc.ce.libs.mappers.policy.PolicyValidator;
import de.sovity.edc.runtime.config.ConfigUtils;
import de.sovity.edc.runtime.simple_di.Service;
import de.sovity.edc.utils.JsonUtils;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import static de.sovity.edc.ce.libs.mappers.dsp.TypeTransformerRegistryUtils.forManagementApiContext;
import static de.sovity.edc.utils.JsonUtils.toJson;

@RequiredArgsConstructor
@Service
public class PolicyMapper {
    private final ExpressionExtractor expressionExtractor;
    private final ExpressionMapper expressionMapper;
    private final TypeTransformerRegistry typeTransformerRegistry;
    private final JsonLd jsonLd;
    private final ConfigUtils configUtils;

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

        var actionType = "USE"; // For some reason this needs to be that, expecting it to become irrelevant in Saturn
        var action = Action.Builder.newInstance().type(actionType).build();

        var permission = Permission.Builder.newInstance()
            .action(action)
            .constraints(constraints.stream().toList())
            .build();

        return Policy.Builder.newInstance()
            .type(PolicyType.SET)
            .permission(permission)
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

        return forManagementApiContext(typeTransformerRegistry).transform(expanded, Policy.class)
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
        return forManagementApiContext(typeTransformerRegistry).transform(policy, JsonObject.class)
            .orElseThrow(FailedMappingException::ofFailure);
    }
}
