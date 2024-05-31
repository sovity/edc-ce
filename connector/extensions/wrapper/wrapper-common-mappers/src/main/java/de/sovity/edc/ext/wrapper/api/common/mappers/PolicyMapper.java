package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.FailedMappingException;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicy;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.utils.JsonUtils;
import jakarta.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;

import java.util.ArrayList;

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
