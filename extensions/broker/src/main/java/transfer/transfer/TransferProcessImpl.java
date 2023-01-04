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
package transfer.transfer;

import org.eclipse.edc.spi.monitor.Monitor;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransferProcessImpl implements TransferProcess {

    private final Long updateInterval;
    private final Synchronizer synchronizer;
    private final Monitor monitor;

    public TransferProcessImpl(
            Synchronizer synchronizer,
            Monitor monitor,
            String updateInterval) {
        this.synchronizer = synchronizer;
        this.monitor = monitor;
        this.updateInterval = Long.parseLong(updateInterval);
    }

    @Override
    public void startTransferProcess(URL brokerBaseUrl) {
        var executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(
                () -> startSynchronize(brokerBaseUrl),
                updateInterval,
                updateInterval,
                TimeUnit.SECONDS);
    }

    private void startSynchronize(URL brokerBaseUrl) {
        try {
            synchronizer.synchronize(brokerBaseUrl);
        } catch (Exception e) {
            monitor.info("Error during synchronizing Broker", e);
        }
    }
}
