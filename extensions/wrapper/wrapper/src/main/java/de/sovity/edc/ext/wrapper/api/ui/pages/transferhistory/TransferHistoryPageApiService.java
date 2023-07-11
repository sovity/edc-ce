/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui.pages.transferhistory;

import de.sovity.edc.ext.wrapper.api.ui.model.TransferHistoryPage;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.asset.Asset;

@RequiredArgsConstructor
public class TransferHistoryPageApiService {

    private final TransferHistoryPageDataFetcher transferHistoryPageDataFetcher;

    private final TransferProcessAssetFetcher transferProcessAssetFetcher;

    public TransferHistoryPage transferHistoryPage() {

        var transferHistoryPageEntries = transferHistoryPageDataFetcher.getTransferHistoryEntries();
        return new TransferHistoryPage(transferHistoryPageEntries);
    }

    public Asset fetchAssetForTransferProcess(String transferProcessId) {

        return transferProcessAssetFetcher.getAssetForTransferHistoryPage(transferProcessId);
    }
}
