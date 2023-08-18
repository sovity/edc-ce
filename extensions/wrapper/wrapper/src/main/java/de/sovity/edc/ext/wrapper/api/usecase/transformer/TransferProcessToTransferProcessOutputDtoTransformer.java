package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.usecase.model.DataRequestDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.TransferProcessOutputDto;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcessStates;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.edc.transform.spi.TypeTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransferProcessToTransferProcessOutputDtoTransformer implements
        TypeTransformer<TransferProcess, TransferProcessOutputDto> {


    @Override
    public Class<TransferProcess> getInputType() {
        return TransferProcess.class;
    }

    @Override
    public Class<TransferProcessOutputDto> getOutputType() {
        return TransferProcessOutputDto.class;
    }

    @Override
    public @Nullable TransferProcessOutputDto transform(@NotNull TransferProcess transferProcess,
            @NotNull TransformerContext context) {
        var dataAddressProperties = transferProcess.getContentDataAddress() == null ?
                null : transferProcess.getContentDataAddress().getProperties();

        var dataRequest = context.transform(transferProcess.getDataRequest(), DataRequestDto.class);
        if (dataRequest == null){
            context.problem().nullProperty().type(TransferProcess.class).property("DataRequest").report();
            return null;
        }


        return TransferProcessOutputDto.builder()
                .id(transferProcess.getId())
                .state(TransferProcessStates.from(transferProcess.getState()).name())
                .dataRequest(dataRequest)
                .contentDataAddress(dataAddressProperties)
                .privateProperties(transferProcess.getPrivateProperties())
                .errorDetail(transferProcess.getErrorDetail())
                .build();
    }
}
