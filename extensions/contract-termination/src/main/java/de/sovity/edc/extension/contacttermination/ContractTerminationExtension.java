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

import de.sovity.edc.extension.contacttermination.query.ContractAgreementIsTerminatedQuery;
import de.sovity.edc.extension.contacttermination.query.ContractAgreementTerminationDetailsQuery;
import de.sovity.edc.extension.contacttermination.query.TerminateContractQuery;
import de.sovity.edc.extension.db.directaccess.DslContextFactory;
import de.sovity.edc.extension.messenger.SovityMessenger;
import de.sovity.edc.extension.messenger.SovityMessengerRegistry;
import lombok.val;
import org.eclipse.edc.connector.transfer.spi.observe.TransferProcessObservable;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.agent.ParticipantAgentService;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;


@Provides(ContractAgreementTerminationService.class)
public class ContractTerminationExtension implements ServiceExtension {

    @Setting(required = true)
    private static final String EDC_PARTICIPANT_ID = "edc.participant.id";

    @Inject
    private DslContextFactory dslContextFactory;

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

        val terminationService = setupTerminationService(context);
        setupMessenger(terminationService);
        setupTransferPrevention();
    }

    private ContractAgreementTerminationService setupTerminationService(ServiceExtensionContext context) {

        val config = context.getConfig();
        val contractAgreementTerminationDetailsQuery = new ContractAgreementTerminationDetailsQuery();
        val terminateContractQuery = new TerminateContractQuery();

        val terminationService = new ContractAgreementTerminationService(
            sovityMessenger,
            contractAgreementTerminationDetailsQuery,
            terminateContractQuery,
            monitor,
            config.getString(EDC_PARTICIPANT_ID)
        );

        context.registerService(ContractAgreementTerminationService.class, terminationService);

        return terminationService;
    }

    private void setupMessenger(ContractAgreementTerminationService terminationService) {
        messengerRegistry.register(
            ContractTerminationMessage.class,
            (claims, termination) ->
                dslContextFactory.transactionResult(dsl ->
                    terminationService.terminateAgreementAsCounterparty(
                        dsl,
                        participantAgentService.createFor(claims).getIdentity(),
                        buildTerminationRequest(termination))));
    }

    private static ContractTerminationParam buildTerminationRequest(ContractTerminationMessage message) {
        return new ContractTerminationParam(
            message.getContractAgreementId(),
            message.getDetail(),
            message.getReason()
        );
    }

    private void setupTransferPrevention() {
        observable.registerListener(
            new TransferProcessBlocker(
                dslContextFactory,
                new ContractAgreementIsTerminatedQuery()));
    }
}
