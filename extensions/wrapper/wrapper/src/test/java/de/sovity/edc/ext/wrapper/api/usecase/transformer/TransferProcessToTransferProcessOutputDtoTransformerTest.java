package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.TransferProcessOutputDto;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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

    }

}
