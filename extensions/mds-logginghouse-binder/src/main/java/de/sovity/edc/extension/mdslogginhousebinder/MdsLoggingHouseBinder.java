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
 *
 */

package de.sovity.edc.extension.mdslogginhousebinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.extension.contacttermination.ContractAgreementTerminationService;
import lombok.val;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.event.EventRouter;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

public class MdsLoggingHouseBinder implements ServiceExtension {

    @Inject
    private EventRouter eventRouter;

    @Inject
    private Monitor monitor;

    @Inject
    private ContractAgreementTerminationService contractAgreementTerminationService;

    @Override
    public void initialize(ServiceExtensionContext context) {
        setupLoggingHouseTerminationEventsLogging();
    }

    private void setupLoggingHouseTerminationEventsLogging() {
        val objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        contractAgreementTerminationService.getContractTerminationObservable()
            .registerListener(new MdsContractTerminationObserver(eventRouter, monitor, objectMapper));
    }
}
