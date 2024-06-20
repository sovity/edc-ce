/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 */

package de.sovity.edc.extension.contactcancellation;

import de.sovity.edc.extension.contactcancellation.controller.ContractCancellationController;
import de.sovity.edc.extension.db.directaccess.DirectDatabaseAccess;
import lombok.val;
import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.connector.transfer.spi.observe.TransferProcessListener;
import org.eclipse.edc.connector.transfer.spi.observe.TransferProcessObservable;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;


public class ContractCancellationExtension implements ServiceExtension {

    @Inject
    private DirectDatabaseAccess directDatabaseAccess;

    @Inject
    private ManagementApiConfiguration managementApiConfiguration;

    @Inject
    private TransferProcessObservable observable;

    @Inject
    private WebService webService;

    @Override
    public void initialize(ServiceExtensionContext context) {

        setupController();

        observable.registerListener(new TransferProcessListener() {
            @Override
            public void preStarted(TransferProcess process) {
                // TODO: maybe cancel it here?
                val contract = process.getContractId();
                // find agreement
                // check ifg cancelled
                // deny

                TransferProcessListener.super.preStarted(process);
            }
        });
    }

    private void setupController() {
        val controller = new ContractCancellationController(directDatabaseAccess.dslContext());
        webService.registerResource(managementApiConfiguration.getContextAlias(), controller);
    }
}
