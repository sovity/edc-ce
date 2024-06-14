package de.sovity.edc.extension.custommessages;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.custommessages.api.MessageHandlerRegistry;
import de.sovity.edc.extension.custommessages.api.SovityMessenger;
import de.sovity.edc.extension.custommessages.controller.CustomMessageReceiverController;
import de.sovity.edc.extension.custommessages.impl.JsonObjectFromGenericSovityMessage;
import de.sovity.edc.extension.custommessages.impl.MessageEmitter;
import de.sovity.edc.extension.custommessages.impl.MessageHandlerRegistryImpl;
import de.sovity.edc.extension.custommessages.impl.MessageReceiver;
import de.sovity.edc.extension.custommessages.impl.ObjectMapperFactory;
import de.sovity.edc.extension.custommessages.impl.SovityMessengerImpl;
import de.sovity.edc.extension.custommessages.impl.SovityMessageRecord;
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
public class CustomMessagesSenderExtension implements ServiceExtension {

    public static final String NAME = "SovityCustomMessages";

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
    public void initialize(ServiceExtensionContext context) {
        val objectMapper = new ObjectMapperFactory().createObjectMapper();
        val handlers = new MessageHandlerRegistryImpl();
        context.registerService(MessageHandlerRegistry.class, handlers);
        setupSovityCustomEmitter(context, objectMapper);
        setupSovityCustomReceiver(objectMapper, handlers);
        typeTransformerRegistry.register(new JsonObjectFromGenericSovityMessage());
    }

    private void setupSovityCustomReceiver(ObjectMapper objectMapper, MessageHandlerRegistry handlers) {
        val receiver = new CustomMessageReceiverController(
            identityService,
            dspApiConfiguration.getDspCallbackAddress(),
            typeTransformerRegistry,
            monitor,
            objectMapper,
            handlers);

        webService.registerResource(dspApiConfiguration.getContextAlias(), receiver);
    }

    private void setupSovityCustomEmitter(ServiceExtensionContext context, ObjectMapper objectMapper) {
        val factory = new MessageEmitter(jsonLdRemoteMessageSerializer);
        val delegate = new MessageReceiver(objectMapper);

        dspHttpRemoteMessageDispatcher.registerMessage(SovityMessageRecord.class, factory, delegate);

        val postOffice = new SovityMessengerImpl(registry, objectMapper);
        context.registerService(SovityMessenger.class, postOffice);
    }
}
