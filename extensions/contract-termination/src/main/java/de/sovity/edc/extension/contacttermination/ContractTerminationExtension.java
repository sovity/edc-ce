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
import org.eclipse.edc.connector.transfer.spi.observe.TransferProcessListener;
import org.eclipse.edc.connector.transfer.spi.observe.TransferProcessObservable;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.agent.ParticipantAgentService;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.Config;

import static de.sovity.edc.extension.contacttermination.MapperUtils.toModel;


public class ContractTerminationExtension implements ServiceExtension {

    @Setting(required = true)
    private static final String EDC_PARTICIPANT_ID = "edc.participant.id";

    @Inject
    private DirectDatabaseAccess directDatabaseAccess;

    @Inject
    private IdentityService identityService;

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

        setupMessenger(context.getConfig());
        setupTransferPrevention();
    }

    private void setupMessenger(Config config) {

        val contractAgreementTerminationDetailsQuery = new ContractAgreementTerminationDetailsQuery(directDatabaseAccess::newDslContext);
        val terminateContractQuery = new TerminateContractQuery(directDatabaseAccess::newDslContext);

        val terminationService = new ContractAgreementTerminationService(
            sovityMessenger,
            contractAgreementTerminationDetailsQuery,
            terminateContractQuery,
            monitor,
            config.getString(EDC_PARTICIPANT_ID)
        );

        messengerRegistry.register(
            ContractTerminationOutgoingMessage.class,
            (claims, termination) -> terminationService.terminateCounterpartyAgreement(
                participantAgentService.createFor(claims).getIdentity(),
                toModel(termination)));
    }

    private void setupTransferPrevention() {
        observable.registerListener(new TransferProcessListener() {

            @Override
            public void preRequesting(TransferProcess process) {
                val dsl = directDatabaseAccess.newDslContext();

                val t = Tables.SOVITY_CONTRACT_TERMINATION;

                val count = dsl
                    .selectCount()
                    .from(t)
                    .where(t.CONTRACT_AGREEMENT_ID.eq(process.getContractId()))
                    .fetchSingle()
                    .value1();

                if (count >= 1) {
                    // ugly solution. Needs support on core EDC side https://github.com/sovity/PMO-Software/issues/1260
                    throw new IllegalStateException();
                }
            }
        });
    }

}
