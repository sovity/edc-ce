package de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.connector.spi.transferprocess.TransferProcessService;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
public class TransferProcessAssetFetcher {
    private final AssetService assetService;

    private final TransferProcessService transferProcessService;

    public Asset getAssetForTransferHistoryPage(String transferProcessId) {
        var transferStream = getAllTransferProcesses().stream();
        var transferProcess = transferStream
                .filter(process -> process.getId().equals(transferProcessId)).findFirst()
                .orElseThrow(() -> new EdcException("Could not find transfer process with ID %s.".formatted(transferProcessId)));
        return getAssetFromTransferProcess(transferProcess);
    }
    @NotNull
    private Asset getAssetFromTransferProcess(TransferProcess process) {
        String assetId = process.getDataRequest().getAssetId();
        return assetService.query(QuerySpec.max()).getContent()
                .filter(assets -> assets.getId().equals(assetId))
                .findFirst().orElseGet(() -> Asset.Builder.newInstance().id(assetId).build());
    }

    @NotNull
    private List<TransferProcess> getAllTransferProcesses() {
        return transferProcessService.query(QuerySpec.max()).getContent().toList();
    }
}
