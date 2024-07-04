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

package de.sovity.edc.extension.messenger;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.messenger.controller.SovityMessageController;
import de.sovity.edc.extension.messenger.impl.JsonObjectFromSovityMessageRequest;
import de.sovity.edc.extension.messenger.impl.JsonObjectFromSovityMessageResponse;
import de.sovity.edc.extension.messenger.impl.MessageEmitter;
import de.sovity.edc.extension.messenger.impl.MessageReceiver;
import de.sovity.edc.extension.messenger.impl.ObjectMapperFactory;
import de.sovity.edc.extension.messenger.impl.SovityMessageRequest;
import lombok.val;
import org.eclipse.edc.protocol.dsp.api.configuration.DspApiConfiguration;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRemoteMessageDispatcher;
import org.eclipse.edc.protocol.dsp.spi.serialization.JsonLdRemoteMessageSerializer;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.agent.ParticipantAgentService;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.WebService;

@Provides({SovityMessenger.class, SovityMessengerRegistry.class})
public class SovityMessengerExtension implements ServiceExtension {

    public static final String NAME = "SovityMessenger";

    @Inject
    private DspApiConfiguration dspApiConfiguration;

    @Inject
    private DspHttpRemoteMessageDispatcher dspHttpRemoteMessageDispatcher;

    @Inject
    private IdentityService identityService;

    @Inject
    private JsonLdRemoteMessageSerializer jsonLdRemoteMessageSerializer;

    @Inject
    private Monitor monitor;

    @Inject
    private RemoteMessageDispatcherRegistry registry;

    @Inject
    private TypeManager typeManager;

    @Inject
    private TypeTransformerRegistry typeTransformerRegistry;

    @Inject
    private WebService webService;

    @Inject
    private ParticipantAgentService participantAgentService;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        val objectMapper = new ObjectMapperFactory().createObjectMapper();
        val handlers = new SovityMessengerRegistry();
        setupSovityMessengerEmitter(context, objectMapper);
        setupSovityMessengerReceiver(context, objectMapper, handlers);
    }

    private void setupSovityMessengerEmitter(ServiceExtensionContext context, ObjectMapper objectMapper) {
        val factory = new MessageEmitter(jsonLdRemoteMessageSerializer);
        val delegate = new MessageReceiver(objectMapper);

        dspHttpRemoteMessageDispatcher.registerMessage(SovityMessageRequest.class, factory, delegate);

        typeTransformerRegistry.register(new JsonObjectFromSovityMessageRequest());

        val sovityMessenger = new SovityMessenger(registry, objectMapper);
        context.registerService(SovityMessenger.class, sovityMessenger);
    }

    private void setupSovityMessengerReceiver(ServiceExtensionContext context, ObjectMapper objectMapper, SovityMessengerRegistry handlers) {
        val receiver = new SovityMessageController(
            identityService,
            dspApiConfiguration.getDspCallbackAddress(),
            typeTransformerRegistry,
            monitor,
            objectMapper,
            participantAgentService,
            handlers);

        webService.registerResource(dspApiConfiguration.getContextAlias(), receiver);

        context.registerService(SovityMessengerRegistry.class, handlers);

        typeTransformerRegistry.register(new JsonObjectFromSovityMessageResponse());
    }
}
