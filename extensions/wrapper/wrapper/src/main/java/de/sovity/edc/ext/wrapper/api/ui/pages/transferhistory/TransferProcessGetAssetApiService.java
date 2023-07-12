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

import de.sovity.edc.ext.wrapper.api.common.model.AssetDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransferProcessGetAssetApiService {

    private final TransferProcessAssetFetcher transferProcessAssetFetcher;

    public AssetDto fetchAssetForTransferProcess(String transferProcessId) {

        return transferProcessAssetFetcher.getAssetForTransferHistoryPage(transferProcessId);
    }
}
