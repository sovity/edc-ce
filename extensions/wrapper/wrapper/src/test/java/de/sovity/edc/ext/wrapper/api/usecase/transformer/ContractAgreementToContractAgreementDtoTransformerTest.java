package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.ContractAgreementDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.policy.model.*;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        var contractAgreement = ContractAgreement.Builder.newInstance()
                .id("id")
                .providerId("provId")
                .consumerId("consumerId")
                .assetId("assetId")
                .policy(Policy.Builder.newInstance().assignee("A").assigner("B").build())
                .build();

        var pDto = new PolicyDto();
        when(context.transform(any(Policy.class),eq(PolicyDto.class))).thenReturn(pDto);
        var result = transformer.transform(contractAgreement,context);

        assertThat(result).isNotNull();
        assertThat(result.getPolicy()).isNotNull();





    }
}
