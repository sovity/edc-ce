package de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory;

import de.sovity.edc.ext.wrapper.api.common.model.AssetDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import static de.sovity.edc.ext.wrapper.utils.EdcDateUtils.utcMillisToOffsetDateTime;
import static de.sovity.edc.ext.wrapper.utils.MapUtils.mapValues;

@RequiredArgsConstructor
public class TransferHistoryPageAssetFetcherService {
    private final AssetService assetService;
    private final TransferProcessService transferProcessService;

    public AssetDto getAssetForTransferHistoryPage(String transferProcessId) {

        var transferProcessById = transferProcessService.findById(transferProcessId);
        if (transferProcessById == null) {
            throw new EdcException("Could not find transfer process with ID %s.".formatted(transferProcessId));
        }
        return getAssetFromTransferProcess(transferProcessById);
    }

    @NotNull
    private AssetDto getAssetFromTransferProcess(TransferProcess process) {
        String assetId = process.getDataRequest().getAssetId();
        var asset = assetService.findById(process.getDataRequest().getAssetId());
        if (asset == null) {
            asset = Asset.Builder.newInstance().id(assetId).build();
        }
        return buildAssetDto(asset);
    }

    @NotNull
    private AssetDto buildAssetDto(@NonNull Asset asset) {
        var createdAt = utcMillisToOffsetDateTime(asset.getCreatedAt());
        var properties = mapValues(asset.getProperties(), Object::toString);
        return new AssetDto(asset.getId(), createdAt, properties);
    }

}
