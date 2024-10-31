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

package de.sovity.edc.extension.messenger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.extension.messenger.controller.SovityMessageController;
import de.sovity.edc.extension.messenger.impl.JsonObjectFromSovityMessageRequest;
import de.sovity.edc.extension.messenger.impl.JsonObjectFromSovityMessageResponse;
import de.sovity.edc.extension.messenger.impl.MessageEmitter;
import de.sovity.edc.extension.messenger.impl.ObjectMapperFactory;
import de.sovity.edc.extension.messenger.impl.SovityMessageRequest;
import de.sovity.edc.extension.messenger.impl.SovityMessageRequestBodyExtractor;
import lombok.val;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.PolicyScope;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.protocol.dsp.http.spi.dispatcher.DspHttpRemoteMessageDispatcher;
import org.eclipse.edc.protocol.dsp.http.spi.serialization.JsonLdRemoteMessageSerializer;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.agent.ParticipantAgentService;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;

@Provides({SovityMessenger.class, SovityMessengerRegistry.class})
public class SovityMessengerExtension implements ServiceExtension {

    public static final String NAME = "SovityMessenger";

    @PolicyScope
    public static final String MESSENGER_SCOPE = "messenger.sovity";

    @Inject
    private DspHttpRemoteMessageDispatcher dspHttpRemoteMessageDispatcher;

    @Inject
    private IdentityService identityService;

    @Inject
    private JsonLdRemoteMessageSerializer jsonLdRemoteMessageSerializer;

    @Inject
    private Monitor monitor;

    @Inject
    private ParticipantAgentService participantAgentService;

    @Inject
    private PolicyEngine policyEngine;

    @Inject
    private RemoteMessageDispatcherRegistry registry;

    @Inject
    private TypeTransformerRegistry typeTransformerRegistry;

    @Inject
    private WebService webService;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        val objectMapper = new ObjectMapperFactory().createObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        val handlers = new SovityMessengerRegistry();
        setupSovityMessengerEmitter(context, objectMapper);
        setupSovityMessengerReceiver(context, objectMapper, handlers);
    }

    private void setupSovityMessengerEmitter(ServiceExtensionContext context, ObjectMapper objectMapper) {
        val factory = new MessageEmitter(jsonLdRemoteMessageSerializer);
        val bodyExtractor = new SovityMessageRequestBodyExtractor(objectMapper);

        dspHttpRemoteMessageDispatcher.registerMessage(SovityMessageRequest.class, factory, bodyExtractor);

        dspHttpRemoteMessageDispatcher.registerPolicyScope(
            SovityMessageRequest.class,
            MESSENGER_SCOPE,
            (ignored) -> Policy.Builder.newInstance().permission(Permission.Builder.newInstance().build()).build());

        policyEngine.registerPostValidator(MESSENGER_SCOPE, (ignored1, ignored2) -> true);

        typeTransformerRegistry.register(new JsonObjectFromSovityMessageRequest());

        val sovityMessenger = new SovityMessenger(registry, objectMapper, monitor);
        context.registerService(SovityMessenger.class, sovityMessenger);
    }

    private void setupSovityMessengerReceiver(
        ServiceExtensionContext context,
        ObjectMapper objectMapper,
        SovityMessengerRegistry handlers
    ) {
        val receiver = new SovityMessageController(
            identityService,
            typeTransformerRegistry,
            monitor,
            objectMapper,
            handlers
        );

        webService.registerResource(ApiContext.PROTOCOL, receiver);

        context.registerService(SovityMessengerRegistry.class, handlers);

        typeTransformerRegistry.register(new JsonObjectFromSovityMessageResponse());
    }
}
