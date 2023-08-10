package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDefinitionDto;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.policy.model.Policy;

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
                .constraints(List.of())
                .errors(new ArrayList<>())
                .build();
    }

    public PolicyDefinitionDto buildPolicyDefinitionDto(PolicyDefinition policyDefinition) {
        return PolicyDefinitionDto.builder().uiPolicyDto(buildPolicyDto(policyDefinition.getPolicy())).build();
    }


    public Policy buildPolicy(UiPolicyDto policyDto) {
        return deserializePolicy(policyDto.getPolicyJsonLd());
    }

    public PolicyDefinition buildPolicyDefinition(PolicyDefinitionDto policyDefinitionDto) {
        return PolicyDefinition.Builder.newInstance().policy(buildPolicy(policyDefinitionDto.getUiPolicyDto())).build();
    }

    @SneakyThrows
    public Policy deserializePolicy(String edcPolicyJsonLd) {
        return jsonLdObjectMapper.readValue(edcPolicyJsonLd, Policy.class);
    }

    @SneakyThrows
    public String serializePolicy(Policy policy) {
        return jsonLdObjectMapper.writeValueAsString(policy);
    }
}
