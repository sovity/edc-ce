/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.contract_termination;

import de.sovity.edc.ce.config.CeConfigProps;
import de.sovity.edc.ce.modules.db.jooq.DslContextFactory;
import de.sovity.edc.ce.modules.messaging.contract_termination.query.ContractAgreementTerminationDetailsQuery;
import de.sovity.edc.ce.modules.messaging.contract_termination.query.TerminateContractQuery;
import de.sovity.edc.ce.modules.messaging.messenger.SovityMessenger;
import de.sovity.edc.ce.modules.messaging.messenger.SovityMessengerRegistry;
import lombok.val;
import org.eclipse.edc.connector.controlplane.contract.spi.policy.TransferProcessPolicyContext;
import org.eclipse.edc.connector.controlplane.transfer.spi.observe.TransferProcessObservable;
import org.eclipse.edc.connector.policy.monitor.spi.PolicyMonitorContext;
import org.eclipse.edc.participant.spi.ParticipantAgentService;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.edc.agreements.retirement.AgreementRetirementValidator;
import org.eclipse.tractusx.edc.agreements.retirement.spi.service.AgreementsRetirementService;

@Provides(ContractAgreementTerminationService.class)
public class ContractTerminationExtension implements ServiceExtension {

    @Inject
    private AgreementsRetirementService service;

    @Inject
    private DslContextFactory dslContextFactory;

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

    @Inject
    private PolicyEngine policyEngine;

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
            CeConfigProps.SOVITY_CONTRACT_TERMINATION_THREAD_POOL_SIZE.getIntOrThrow(config),
            sovityMessenger,
            contractAgreementTerminationDetailsQuery,
            terminateContractQuery,
            monitor,
            CeConfigProps.getEDC_PARTICIPANT_ID().getStringOrEmpty(config)
        );

        context.registerService(ContractAgreementTerminationService.class, terminationService);

        return terminationService;
    }

    private void setupMessenger(ContractAgreementTerminationService terminationService) {
        messengerRegistry.register(
            ContractTerminationMessage.class,
            (participantAgent, termination) ->
                dslContextFactory.transactionResult(dsl ->
                    terminationService.terminateAgreementAsCounterparty(
                        dsl,
                        participantAgent.getIdentity(),
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
        var validator = new AgreementRetirementValidator(service);
        policyEngine.registerPreValidator(TransferProcessPolicyContext.class, validator.transferProcess());
        policyEngine.registerPreValidator(PolicyMonitorContext.class, validator.policyMonitor());
    }
}
