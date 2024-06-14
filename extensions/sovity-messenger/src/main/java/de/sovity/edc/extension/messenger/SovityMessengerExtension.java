package de.sovity.edc.extension.messenger;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.messenger.api.MessageHandlerRegistry;
import de.sovity.edc.extension.messenger.api.SovityMessenger;
import de.sovity.edc.extension.messenger.controller.CustomMessageReceiverController;
import de.sovity.edc.extension.messenger.impl.JsonObjectFromSovityMessageRequest;
import de.sovity.edc.extension.messenger.impl.JsonObjectFromSovityMessageResponse;
import de.sovity.edc.extension.messenger.impl.MessageEmitter;
import de.sovity.edc.extension.messenger.impl.MessageHandlerRegistryImpl;
import de.sovity.edc.extension.messenger.impl.MessageReceiver;
import de.sovity.edc.extension.messenger.impl.ObjectMapperFactory;
import de.sovity.edc.extension.messenger.impl.SovityMessageRequest;
import de.sovity.edc.extension.messenger.impl.SovityMessengerImpl;
import lombok.val;
import org.eclipse.edc.protocol.dsp.api.configuration.DspApiConfiguration;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRemoteMessageDispatcher;
import org.eclipse.edc.protocol.dsp.spi.serialization.JsonLdRemoteMessageSerializer;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.eclipse.edc.web.spi.WebService;

@Provides({SovityMessenger.class, MessageHandlerRegistry.class})
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

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        val objectMapper = new ObjectMapperFactory().createObjectMapper();
        val handlers = new MessageHandlerRegistryImpl();
        context.registerService(MessageHandlerRegistry.class, handlers);
        setupSovityMessengerEmitter(context, objectMapper);
        setupSovityMessengerReceiver(objectMapper, handlers);
    }

    private void setupSovityMessengerEmitter(ServiceExtensionContext context, ObjectMapper objectMapper) {
        val factory = new MessageEmitter(jsonLdRemoteMessageSerializer);
        val delegate = new MessageReceiver(objectMapper);

        dspHttpRemoteMessageDispatcher.registerMessage(SovityMessageRequest.class, factory, delegate);

        typeTransformerRegistry.register(new JsonObjectFromSovityMessageRequest());

        val sovityMessenger = new SovityMessengerImpl(registry, objectMapper);
        context.registerService(SovityMessenger.class, sovityMessenger);
    }

    private void setupSovityMessengerReceiver(ObjectMapper objectMapper, MessageHandlerRegistry handlers) {
        val receiver = new CustomMessageReceiverController(identityService, dspApiConfiguration.getDspCallbackAddress(), typeTransformerRegistry, monitor, objectMapper, handlers);

        webService.registerResource(dspApiConfiguration.getContextAlias(), receiver);

        typeTransformerRegistry.register(new JsonObjectFromSovityMessageResponse());
    }
}
