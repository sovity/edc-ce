package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.ContractAgreementDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractNegotiationOutputDto;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

public class ContractNegotiationToContractNegotiationOutputDtoTransformerTest {
    private final ContractNegotiationToContractNegotiationOutputDtoTransformer transformer = new ContractNegotiationToContractNegotiationOutputDtoTransformer();
    private final TransformerContext context = mock(TransformerContext.class);

    @Test
    void types() {
        assertThat(transformer.getInputType()).isEqualTo(ContractNegotiation.class);
        assertThat(transformer.getOutputType()).isEqualTo(ContractNegotiationOutputDto.class);
    }

    @Test
    void transform(){

        var contractNegotiation = ContractNegotiation.Builder.newInstance()
                .contractAgreement(ContractAgreement.Builder.newInstance()
                        .id("id")
                        .assetId("assetId")
                        .policy(Policy.Builder.newInstance().type(PolicyType.CONTRACT).assignee("A").assigner("B").target("target").build())
                        .providerId("provId")
                        .consumerId("consId")
                        .policy(Policy.Builder.newInstance().assignee("A").assigner("B").build())
                        .build())
                .contractOffer(ContractOffer.Builder.newInstance().id("id").assetId("assetId").providerId("provId").policy(Policy.Builder.newInstance().assignee("A").assigner("B").build()).build())
                .contractAgreement(ContractAgreement.Builder.newInstance().id("id").providerId("provId").consumerId("consumerId").assetId("assetId").policy(Policy.Builder.newInstance().assignee("A").assigner("B").build()).build())
                .type(ContractNegotiation.Type.CONSUMER)
                .id("id")
                .protocol("DSP")
                .correlationId("co")
                .counterPartyAddress("add")
                .counterPartyId("counterId")
                .build();

        var caDto = new ContractAgreementDto();
        when(context.transform(any(ContractAgreement.class),eq(ContractAgreementDto.class))).thenReturn(caDto);
        var result = transformer.transform(contractNegotiation,context);

        assertThat(result).isNotNull();
        assertThat(result.getContractAgreement()).isNotNull();



    }
}
