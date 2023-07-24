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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        TransferProcess transferProcess = TransferProcess.Builder.newInstance()
                .contentDataAddress(DataAddress.Builder.newInstance().type("content").build())
                .dataRequest(DataRequest.Builder.newInstance()
                        .id("id")
                        .processId("ReqId")
                        .protocol("dsp")
                        .dataDestination(DataAddress.Builder.newInstance().type("DEST").build())
                        .contractId("contractId")
                        .connectorId("connectorId")
                        .assetId("assetId")
                        .connectorAddress("localhost")
                        .build())
                .type(TransferProcess.Type.CONSUMER).id("procId").build();


        var drDto = new DataRequestDto();
        when(context.transform(any(DataRequest.class), eq(DataRequestDto.class))).thenReturn(drDto);

        var result = transformer.transform(transferProcess,context);

        assertThat(result).isNotNull();
        assertThat(result.getDataRequest()).isNotNull();

    }

}
