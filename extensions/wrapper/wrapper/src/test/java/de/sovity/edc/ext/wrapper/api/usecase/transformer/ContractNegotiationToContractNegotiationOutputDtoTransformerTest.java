package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.ContractAgreementDto;
import de.sovity.edc.ext.wrapper.api.common.model.ExpressionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractNegotiationOutputDto;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.transform.spi.NullPropertyBuilder;
import org.eclipse.edc.transform.spi.ProblemBuilder;
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
        var contractAgreement = ContractAgreement.Builder.newInstance()
                .id("contId")
                .assetId("assetId")
                .policy(Policy.Builder.newInstance().type(PolicyType.CONTRACT).assignee("A").assigner("B").target("target").build())
                .providerId("provId")
                .consumerId("consId")
                .policy(Policy.Builder.newInstance().assignee("A").assigner("B").build())
                .contractSigningDate(9999)
                .build();

        var contractNegotiation = ContractNegotiation.Builder.newInstance()
                .contractOffer(ContractOffer.Builder.newInstance().id("id").assetId("assetId").policy(Policy.Builder.newInstance().assignee("A").assigner("B").build()).build())
                .contractAgreement(contractAgreement)
                .type(ContractNegotiation.Type.CONSUMER)
                .id("id")
                .protocol("dsp")
                .correlationId("corrId")
                .counterPartyAddress("counterAddress")
                .counterPartyId("counterId")
                .state(0)
                .build();

        var caDto = ContractAgreementDto.builder().id("contId").providerId("provId")
                .consumerId("consId").contractSigningDate(9999).assetId("assetId")
                .policy(PolicyDto.builder()
                        .legacyPolicy("legacyPol")
                        .permission(PermissionDto.builder().constraints(new ExpressionDto()).build())
                        .build())
                .build();

        when(context.transform(any(ContractAgreement.class),eq(ContractAgreementDto.class))).thenReturn(caDto);
        var result = transformer.transform(contractNegotiation,context);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id");
        assertThat(result.getProtocol()).isEqualTo("dsp");
        assertThat(result.getState()).isEqualTo("INITIAL");
        assertThat(result.getCorrelationId()).isEqualTo("corrId");
        assertThat(result.getCounterPartyId()).isEqualTo("counterId");
        assertThat(result.getCounterPartyAddress()).isEqualTo("counterAddress");

        assertThat(result.getContractAgreement()).isNotNull();
        assertThat(result.getContractAgreement().getId()).isEqualTo("contId");
        assertThat(result.getContractAgreement().getProviderId()).isEqualTo("provId");
        assertThat(result.getContractAgreement().getConsumerId()).isEqualTo("consId");
        assertThat(result.getContractAgreement().getContractSigningDate()).isEqualTo(9999);
        assertThat(result.getContractAgreement().getAssetId()).isEqualTo("assetId");

        verify(context).transform(contractAgreement, ContractAgreementDto.class);

    }

    @Test
    void failingTransform(){
        var contractAgreement = ContractAgreement.Builder.newInstance()
                .id("contId")
                .assetId("assetId")
                .policy(Policy.Builder.newInstance().type(PolicyType.CONTRACT).assignee("A").assigner("B").target("target").build())
                .providerId("provId")
                .consumerId("consId")
                .policy(Policy.Builder.newInstance().assignee("A").assigner("B").build())
                .contractSigningDate(9999)
                .build();

        var contractNegotiation = ContractNegotiation.Builder.newInstance()
                .contractOffer(ContractOffer.Builder.newInstance().id("id").assetId("assetId").policy(Policy.Builder.newInstance().assignee("A").assigner("B").build()).build())
                .contractAgreement(contractAgreement)
                .type(ContractNegotiation.Type.CONSUMER)
                .id("id")
                .protocol("dsp")
                .correlationId("corrId")
                .counterPartyAddress("counterAddress")
                .counterPartyId("counterId")
                .state(0)
                .build();


        var problemBuilder = mock(ProblemBuilder.class);
        var nullPropBuilder = mock(NullPropertyBuilder.class);
        when(context.problem()).thenReturn(problemBuilder);
        when(problemBuilder.nullProperty()).thenReturn(nullPropBuilder);
        when(nullPropBuilder.type(any(Class.class))).thenReturn(nullPropBuilder);
        when(nullPropBuilder.property(anyString())).thenReturn(nullPropBuilder);

        when(context.transform(any(ContractAgreement.class),eq(ContractAgreementDto.class))).thenReturn(null);

        var result = transformer.transform(contractNegotiation,context);

        assertThat(result).isNull();

        verify(context).transform(contractAgreement, ContractAgreementDto.class);

    }
}
