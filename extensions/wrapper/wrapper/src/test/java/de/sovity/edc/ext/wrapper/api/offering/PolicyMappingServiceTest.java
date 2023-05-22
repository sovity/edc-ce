package de.sovity.edc.ext.wrapper.api.offering;

import static org.assertj.core.api.Assertions.*;

import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyTypeDto;
import de.sovity.edc.ext.wrapper.api.offering.services.PolicyMappingService;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.junit.jupiter.api.Test;

public class PolicyMappingServiceTest {

    private final PolicyMappingService mappingService = new PolicyMappingService();

    @Test
    void policyDtoToPolicy_validPolicyDto_returnPolicy() {
        // arrange
        PolicyDto dto = new PolicyDto(PolicyTypeDto.SET, new PermissionDto("USE"));

        // act
        Policy policy = mappingService.policyDtoToPolicy(dto);

        // assert
        assertThat(policy.getType()).isEqualByComparingTo(PolicyType.SET);
        assertThat(policy.getPermissions().size()).isEqualTo(1);
        assertThat(policy.getPermissions().get(0).getAction().getType()).isEqualTo("USE");
    }

}
