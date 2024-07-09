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

package de.sovity.edc.extension.contacttermination;

import de.sovity.edc.extension.contacttermination.query.ContractAgreementTerminationDetailsQuery;
import de.sovity.edc.extension.contacttermination.query.TerminateContractQuery;
import de.sovity.edc.extension.db.directaccess.DirectDatabaseAccess;
import de.sovity.edc.extension.messenger.SovityMessenger;
import de.sovity.edc.extension.messenger.SovityMessengerRegistry;
import lombok.val;
import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.connector.transfer.spi.observe.TransferProcessListener;
import org.eclipse.edc.connector.transfer.spi.observe.TransferProcessObservable;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.protocol.dsp.api.configuration.DspApiConfiguration;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.agent.ParticipantAgentService;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import static de.sovity.edc.extension.contacttermination.MapperUtils.toModel;

// TODO "contract cancellation" is more used than "contract termination"
//  https://trends.google.com/trends/explore?date=today%205-y&q=cancel%20contract,terminate%20contract,abrogate%20contract,annul%20contract&hl=en-US
public class ContractTerminationExtension implements ServiceExtension {

    @Setting(defaultValue = "256")
    public static final String MY_EDC_CONTRACT_CANCELLATION_MAX_REASON_LENGTH = "my.edc.contract.cancellation.max.reason.length";

    @Setting(defaultValue = "1000000")
    public static final String MY_EDC_CONTRACT_CANCELLATION_MAX_DETAIL_LENGTH = "my.edc.contract.cancellation.max.detail.length";

    @Inject
    private DirectDatabaseAccess directDatabaseAccess;

    @Inject
    private IdentityService identityService;

    @Inject
    private DspApiConfiguration dspApiConfiguration;

    @Inject
    private ManagementApiConfiguration managementApiConfiguration;

    @Inject
    private Monitor monitor;

    @Inject
    private SovityMessenger sovityMessenger;

    @Inject
    private SovityMessengerRegistry messengerRegistry;

    @Inject
    private TransferProcessObservable observable;

    @Inject
    private ParticipantAgentService participantAgentService;

    @Override
    public void initialize(ServiceExtensionContext context) {

        setupMessenger();

        observable.registerListener(new TransferProcessListener() {
            @Override
            public void preStarted(TransferProcess process) {
                // TODO: maybe cancel it here?
                // val contract = process.transitionTerminated();
                // find agreement
                // check ifg cancelled
                // deny

                TransferProcessListener.super.preStarted(process);
            }
        });
    }

    private void setupMessenger() {

        // TODO: requires to be until all the modules are initialized?
        // TODO: some test to assert that it's available

        val contractAgreementTerminationDetailsQuery = new ContractAgreementTerminationDetailsQuery(directDatabaseAccess::newDslContext);
        val terminateContractQuery = new TerminateContractQuery(directDatabaseAccess::newDslContext);

        val terminator = new ContractAgreementTerminationService(
            sovityMessenger,
            contractAgreementTerminationDetailsQuery,
            terminateContractQuery,
            monitor);

        messengerRegistry.register(
            ContractTerminationOutgoingMessage.class,
            (claims, termination) -> terminator.secureTerminateContractAgreement(participantAgentService.createFor(claims).getIdentity(), toModel(termination)));
    }

}
