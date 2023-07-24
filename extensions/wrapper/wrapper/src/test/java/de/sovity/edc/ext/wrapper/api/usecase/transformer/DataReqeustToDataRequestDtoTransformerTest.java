package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.usecase.model.ContractNegotiationOutputDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.DataRequestDto;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DataReqeustToDataRequestDtoTransformerTest {

    private final DataRequestToDataRequestDtoTransformer transformer = new DataRequestToDataRequestDtoTransformer();
    private final TransformerContext context = mock(TransformerContext.class);

    @Test
    void types() {
        assertThat(transformer.getInputType()).isEqualTo(DataRequest.class);
        assertThat(transformer.getOutputType()).isEqualTo(DataRequestDto.class);
    }

    @Test
    void transform(){

        var dest = DataAddress.Builder.newInstance().type("DEST").build();
        var dataRequest = DataRequest.Builder.newInstance()
                .id("id")
                .processId("procId")
                .protocol("dsp")
                .dataDestination(dest)
                .contractId("contractId")
                .connectorId("connectorId")
                .assetId("assetId")
                .connectorAddress("localhost")
                .build();

        var result = transformer.transform(dataRequest,context);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("id");
        assertThat(result.getProcessId()).isEqualTo("procId");
        assertThat(result.getProtocol()).isEqualTo("dsp");
        assertThat(result.getDataDestination()).isNotNull();
        assertThat(result.getContractId()).isEqualTo("contractId");
        assertThat(result.getConnectorId()).isEqualTo("connectorId");
        assertThat(result.getAssetId()).isEqualTo("assetId");
        assertThat(result.getConnectorAddress()).isEqualTo("localhost");





    }
}
