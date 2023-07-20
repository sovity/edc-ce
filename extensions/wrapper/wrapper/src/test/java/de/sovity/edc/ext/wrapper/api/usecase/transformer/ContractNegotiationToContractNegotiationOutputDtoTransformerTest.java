package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.ContractAgreementDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractNegotiationOutputDto;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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

    }
}
