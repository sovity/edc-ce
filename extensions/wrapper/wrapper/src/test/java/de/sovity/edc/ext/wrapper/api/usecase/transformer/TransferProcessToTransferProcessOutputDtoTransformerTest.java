package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.DataRequestDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.TransferProcessOutputDto;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TransferProcessToTransferProcessOutputDtoTransformerTest {

    private final TransferProcessToTransferProcessOutputDtoTransformer transformer = new TransferProcessToTransferProcessOutputDtoTransformer();
    private final TransformerContext context = mock(TransformerContext.class);

    @Test
    void types() {
        assertThat(transformer.getInputType()).isEqualTo(TransferProcess.class);
        assertThat(transformer.getOutputType()).isEqualTo(TransferProcessOutputDto.class);
    }

    @Test
    void transform(){
        var dataDest = DataAddress.Builder.newInstance().type("DEST").build();

        var dataReq = DataRequest.Builder.newInstance()
                .id("id")
                .processId("procId")
                .protocol("dsp")
                .dataDestination(dataDest)
                .contractId("contractId")
                .connectorId("connectorId")
                .assetId("assetId")
                .connectorAddress("localhost")
                .build();

        var transferProcess = TransferProcess.Builder.newInstance()
                .contentDataAddress(DataAddress.Builder.newInstance().type("content").build())
                .dataRequest(dataReq)
                .type(TransferProcess.Type.CONSUMER)
                .id("procId").build();


        var drDto = DataRequestDto.builder()
                .id("id")
                .processId("procId")
                .protocol("dsp")
                .contractId("contractId")
                .connectorId("connectorId")
                .assetId("assetId")
                .connectorAddress("localhost")
                .dataDestination(dataDest.getProperties())
                .build();
        when(context.transform(any(DataRequest.class), eq(DataRequestDto.class))).thenReturn(drDto);

        var result = transformer.transform(transferProcess,context);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("procId");
        assertThat(result.getState()).isEqualTo("INITIAL");
        assertThat(result.getContentDataAddress().get("https://w3id.org/edc/v0.0.1/ns/type")).isEqualTo("content");

        assertThat(result.getDataRequest().getId()).isEqualTo("id");
        assertThat(result.getDataRequest().getProcessId()).isEqualTo("procId");
        assertThat(result.getDataRequest().getConnectorAddress()).isEqualTo("localhost");
        assertThat(result.getDataRequest().getProtocol()).isEqualTo("dsp");
        assertThat(result.getDataRequest().getConnectorId()).isEqualTo("connectorId");
        assertThat(result.getDataRequest().getAssetId()).isEqualTo("assetId");
        assertThat(result.getDataRequest().getContractId()).isEqualTo("contractId");
        assertThat(result.getDataRequest().getDataDestination().get("https://w3id.org/edc/v0.0.1/ns/type")).isEqualTo("DEST");


        assertThat(result.getDataRequest()).isNotNull();

        verify(context).transform(dataReq, DataRequestDto.class);

    }

}
