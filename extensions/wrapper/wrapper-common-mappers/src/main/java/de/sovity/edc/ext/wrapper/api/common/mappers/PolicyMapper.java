package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.PolicyValidator;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;

import java.util.ArrayList;

@RequiredArgsConstructor
public class PolicyMapper {
    /**
     * This Object Mapper must be able to handle JSON-LD serialization / deserialization.
     */
    private final ObjectMapper jsonLdObjectMapper;
    private final ConstraintExtractor constraintExtractor;
    private final AtomicConstraintMapper atomicConstraintMapper;

    /**
     * Builds a simplified UI Policy Model from an ODRL Policy.
     * <p>
     * This operation is lossy.
     *
     * @param policy ODRL policy
     * @return ui policy
     */
    @SneakyThrows
    public UiPolicyDto buildPolicyDto(Policy policy) {
        MappingErrors errors = MappingErrors.root();

        var constraints = constraintExtractor.getPermissionConstraints(policy, errors);

        return UiPolicyDto.builder()
                .policyJsonLd(jsonLdObjectMapper.writeValueAsString(policy))
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
     * Builds an ODRL Policy from JSON-LD.
     * <p>
     * This operation is lossless.
     *
     * @param policyJsonLd policy
     * @return ODRL policy
     */
    @SneakyThrows
    public Policy buildPolicy(String policyJsonLd) {
        return jsonLdObjectMapper.readValue(policyJsonLd, Policy.class);
    }
}
