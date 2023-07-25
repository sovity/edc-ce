package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.ContractAgreementDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.policy.model.*;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ContractAgreementToContractAgreementDtoTransformerTest {
    private final ContractAgreementToContractAgreementDtoTransformer transformer = new ContractAgreementToContractAgreementDtoTransformer();
    private final TransformerContext context = mock(TransformerContext.class);

    @Test
    void types() {
        assertThat(transformer.getInputType()).isEqualTo(ContractAgreement.class);
        assertThat(transformer.getOutputType()).isEqualTo(ContractAgreementDto.class);
    }

    @Test
    void transform(){
        var policy = Policy.Builder.newInstance().assignee("A").assigner("B").build();

        var contractAgreement = ContractAgreement.Builder.newInstance()
                .id("id")
                .providerId("provId")
                .consumerId("consumerId")
                .assetId("assetId")
                .policy(policy)
                .build();

        var pDto = new PolicyDto();
        when(context.transform(any(Policy.class),eq(PolicyDto.class))).thenReturn(pDto);
        var result = transformer.transform(contractAgreement,context);

        assertThat(result).isNotNull();
        assertThat(result.getPolicy()).isNotNull();
        assertThat(result.getId()).isEqualTo("id");
        assertThat(result.getProviderId()).isEqualTo("provId");
        assertThat(result.getConsumerId()).isEqualTo("consumerId");
        assertThat(result.getAssetId()).isEqualTo("assetId");

        verify(context).transform(policy, PolicyDto.class);
    }
}
