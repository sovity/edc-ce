package de.sovity.edc.ext.wrapper.api.common.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateDto;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

    public Policy buildPolicy(UiPolicyCreateDto policyCreateDto) {
        // TODO fully implement
        return Policy.Builder.newInstance().type(PolicyType.SET).build();
    }

    @SneakyThrows
    public String serializePolicy(Policy policy) {
        return jsonLdObjectMapper.writeValueAsString(policy);
    }
}
