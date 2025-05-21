/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.sovity.edc.ce.modules.messaging.messenger.controller.SovityMessageController;
import de.sovity.edc.ce.modules.messaging.messenger.impl.JsonObjectFromSovityMessageRequest;
import de.sovity.edc.ce.modules.messaging.messenger.impl.JsonObjectFromSovityMessageResponse;
import de.sovity.edc.ce.modules.messaging.messenger.impl.ObjectMapperFactory;
import de.sovity.edc.ce.modules.messaging.messenger.impl.SovityMessageRequest;
import de.sovity.edc.ce.modules.messaging.messenger.impl.SovityMessageRequestBodyExtractor;
import de.sovity.edc.ce.modules.messaging.messenger.impl.SovityMessageRequestFactory;
import lombok.val;
import org.eclipse.edc.connector.controlplane.services.spi.protocol.ProtocolTokenValidator;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.protocol.dsp.http.spi.dispatcher.DspHttpRemoteMessageDispatcher;
import org.eclipse.edc.protocol.dsp.http.spi.serialization.JsonLdRemoteMessageSerializer;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.eclipse.tractusx.edc.iam.iatp.scope.DefaultScopeExtractor;

import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.eclipse.tractusx.edc.iam.iatp.IatpDefaultScopeExtension.TX_IATP_DEFAULT_SCOPE_PREFIX;


/**
 * See /docs/dev/sovity-messenger.md for an overview of the feature.
 */
@Provides({SovityMessenger.class, SovityMessengerRegistry.class})
public class SovityMessengerExtension implements ServiceExtension {

    public static final String NAME = "SovityMessenger";

    @Inject
    private DspHttpRemoteMessageDispatcher dspHttpRemoteMessageDispatcher;

    @Inject
    private ProtocolTokenValidator protocolTokenValidator;

    @Inject
    private JsonLdRemoteMessageSerializer jsonLdRemoteMessageSerializer;

    @Inject
    private Monitor monitor;

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
        val factory = new SovityMessageRequestFactory(jsonLdRemoteMessageSerializer);
        val bodyExtractor = new SovityMessageRequestBodyExtractor(objectMapper);

        var config = context.getConfig(TX_IATP_DEFAULT_SCOPE_PREFIX);
        var scopes = config.partition().map(this::createScope).collect(Collectors.toSet());

        dspHttpRemoteMessageDispatcher.registerPolicyScope(
            SovityMessageRequest.class, (message) -> Policy.Builder.newInstance().build(),
            new SovityMessagePolicyProvider()
        );
        dspHttpRemoteMessageDispatcher.registerMessage(SovityMessageRequest.class, factory, bodyExtractor);
        policyEngine.registerPostValidator(SovityMessagePolicyContext.class, new DefaultScopeExtractor<>(scopes));
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
            protocolTokenValidator,
            typeTransformerRegistry,
            monitor,
            objectMapper,
            handlers
        );

        webService.registerResource(ApiContext.PROTOCOL, receiver);

        context.registerService(SovityMessengerRegistry.class, handlers);

        typeTransformerRegistry.register(new JsonObjectFromSovityMessageResponse());
    }

    private String createScope(Config config) {
        val alias = config.getString("alias");
        val type = config.getString("type");
        val operation = config.getString("operation");

        return format("%s:%s:%s", alias, type, operation);
    }
}
