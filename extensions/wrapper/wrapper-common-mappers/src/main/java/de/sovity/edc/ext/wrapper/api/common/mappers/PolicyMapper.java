package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDefinitionDto;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PolicyMapper {
    /**
     * This Object Mapper must be able to handle JSON-LD serialization / deserialization.
     */
    private final ObjectMapper jsonLdObjectMapper;

    public UiPolicyDto buildPolicyDto(Policy policy) {
        return UiPolicyDto.builder()
                .policyJsonLd(serializePolicy(policy))
                // TODO fully implement
                .constraints(List.of())
                .errors(new ArrayList<>())
                .build();
    }
    public PolicyDefinitionDto buildPolicyDefinitionDto(PolicyDefinition policyDefinition) {
        return PolicyDefinitionDto.builder().uiPolicyDto(buildPolicyDto(policyDefinition.getPolicy())).build();
    }

    public PolicyDefinition buildPolicyDefinition(PolicyDefinitionCreateRequest policyDefinitionDto) {
        return PolicyDefinition.Builder.newInstance().policy(buildPolicy(policyDefinitionDto.getUiPolicyDto())).build();
    }

    @SneakyThrows
    public Policy deserializePolicy(String edcPolicyJsonLd) {
        return jsonLdObjectMapper.readValue(edcPolicyJsonLd, Policy.class);
    }

    public Policy buildPolicy(UiPolicyCreateRequest policyCreateDto) {
        // TODO fully implement
        return Policy.Builder.newInstance().type(PolicyType.SET).build();
    }

    @SneakyThrows
    public String serializePolicy(Policy policy) {
        return jsonLdObjectMapper.writeValueAsString(policy);
    }
}
