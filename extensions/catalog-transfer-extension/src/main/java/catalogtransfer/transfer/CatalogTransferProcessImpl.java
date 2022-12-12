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
package catalogtransfer.transfer;

import org.eclipse.edc.spi.monitor.Monitor;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CatalogTransferProcessImpl implements CatalogTransferProcess {

    private final Long updateInterval;
    private final CatalogSynchronizer catalogSynchronizer;
    private final Monitor monitor;

    public CatalogTransferProcessImpl(
            CatalogSynchronizer catalogSynchronizer,
            Monitor monitor,
            String updateInterval) {
        this.catalogSynchronizer = catalogSynchronizer;
        this.monitor = monitor;
        this.updateInterval = Long.parseLong(updateInterval);
    }

    @Override
    public void startTransferProcess(URL brokerBaseUrl) {
        var executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(
                () -> startTransfer(brokerBaseUrl),
                updateInterval,
                updateInterval,
                TimeUnit.SECONDS);
    }

    private void startTransfer(URL brokerBaseUrl) {
        try {
            catalogSynchronizer.synchronizeOffers(brokerBaseUrl);
        } catch (Exception e) {
            monitor.info("Error during catalog transfer", e);
        }
    }
}
