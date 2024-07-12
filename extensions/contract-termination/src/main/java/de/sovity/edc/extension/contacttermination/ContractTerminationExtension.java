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

package de.sovity.edc.extension.contacttermination;

import de.sovity.edc.ext.db.jooq.Tables;
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
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.agent.ParticipantAgentService;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import static de.sovity.edc.extension.contacttermination.MapperUtils.toModel;


public class ContractTerminationExtension implements ServiceExtension {

    @Inject
    private DirectDatabaseAccess directDatabaseAccess;

    @Inject
    private IdentityService identityService;

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
            public void preRequesting(TransferProcess process) {
                val dsl = directDatabaseAccess.getDslContext();

                val t = Tables.SOVITY_CONTRACT_TERMINATION;

                val count = dsl
                    .selectCount()
                    .from(t)
                    .where(t.CONTRACT_AGREEMENT_ID.eq(process.getContractId()))
                    .fetchSingle()
                    .value1();

                if (count >= 1) {
                    // TODO: ugly solution. Needs support on core EDC side
                    // TODO: how does this show up on the UI
                    // TODO: if not good, also set the state to terminated
                    throw new IllegalStateException();
                }
            }

            @Override
            public void initiated(TransferProcess process) {

            }
        });
    }

    private void setupMessenger() {

        val contractAgreementTerminationDetailsQuery = new ContractAgreementTerminationDetailsQuery(directDatabaseAccess::newDslContext);
        val terminateContractQuery = new TerminateContractQuery(directDatabaseAccess::newDslContext);

        val terminator = new ContractAgreementTerminationService(
            sovityMessenger,
            contractAgreementTerminationDetailsQuery,
            terminateContractQuery,
            monitor);

        messengerRegistry.register(
            ContractTerminationOutgoingMessage.class,
            (claims, termination) -> terminator.terminateCounterpartyAgreement(
                participantAgentService.createFor(claims).getIdentity(),
                toModel(termination)));
    }

}
