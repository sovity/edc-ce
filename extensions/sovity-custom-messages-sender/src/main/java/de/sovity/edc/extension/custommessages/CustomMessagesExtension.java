package de.sovity.edc.extension.custommessages;

import de.sovity.edc.extension.custommessages.echo.EchoDispatcherDelegate;
import de.sovity.edc.extension.custommessages.echo.EchoMessage;
import de.sovity.edc.extension.custommessages.echo.EchoRequestFactory;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.http.EdcHttpClient;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenDecorator;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

@Provides(SovityMessageDispatcher.class)
public class CustomMessagesExtension implements ServiceExtension {

    public static final String NAME = "SovityCustomMessages";
    private static final String TYPE_MANAGER_SERIALIZER_KEY = "sovity-custom";

    @Inject
    private TypeManager typeManager;

    @Inject
    private RemoteMessageDispatcherRegistry dispatcherRegistry;

    @Inject
    private Monitor monitor;

    @Inject
    private EdcHttpClient httpClient;

    @Inject
    private IdentityService identityService;

    @Inject
    private TokenDecorator tokenDecorator;

    private SovityMessageDispatcher sovityMessageDispatcher;

    @Override
    public void initialize(ServiceExtensionContext context) {
        sovityMessageDispatcher = new SovityMessageDispatcherImpl(httpClient, identityService, tokenDecorator);
        sovityMessageDispatcher.registerMessage(EchoMessage.class, new EchoRequestFactory(), new EchoDispatcherDelegate());
        context.registerService(SovityMessageDispatcher.class, sovityMessageDispatcher);

        dispatcherRegistry.register(sovityMessageDispatcher);
    }
}
