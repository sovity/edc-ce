package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.usecase.model.DataRequestDto;
import org.eclipse.edc.connector.transfer.spi.types.DataRequest;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.edc.transform.spi.TypeTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataRequestToDataRequestDtoTransformer implements TypeTransformer<DataRequest, DataRequestDto> {
    @Override
    public Class<DataRequest> getInputType() {
        return DataRequest.class;
    }

    @Override
    public Class<DataRequestDto> getOutputType() {
        return DataRequestDto.class;
    }

    @Override
    public @Nullable DataRequestDto transform(@NotNull DataRequest dataRequest, @NotNull TransformerContext context) {
        return DataRequestDto.builder()
                .id(dataRequest.getId())
                .processId(dataRequest.getProcessId())
                .connectorId(dataRequest.getConnectorId())
                .connectorAddress(dataRequest.getConnectorAddress())
                .protocol(dataRequest.getProtocol())
                .assetId(dataRequest.getAssetId())
                .contractId(dataRequest.getContractId())
                .dataDestination(dataRequest.getDataDestination().getProperties())
                .build();
    }
}
