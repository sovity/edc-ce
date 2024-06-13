package de.sovity.edc.extension.custommessage.receiver;

import lombok.val;
import org.eclipse.edc.connector.api.management.configuration.ManagementApiConfiguration;
import org.eclipse.edc.protocol.dsp.api.configuration.DspApiConfiguration;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.http.EdcHttpClient;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.web.spi.WebService;


public class SovityCustomMessageReceiverExtension implements ServiceExtension {

    public static final String NAME = "SovityCustomMessageReceiver";

    @Inject
    private TypeManager typeManager;

    @Inject
    private DspApiConfiguration apiConfiguration;

    @Inject
    private RemoteMessageDispatcherRegistry dispatcherRegistry;

    @Inject
    private ManagementApiConfiguration managementApiConfiguration;

    @Inject
    private Monitor monitor;

    @Inject
    private EdcHttpClient httpClient;

    @Inject
    private WebService webService;

    @Inject
    private IdentityService identityService;

    @Override
    public void initialize(ServiceExtensionContext context) {
        val callbackAddress = apiConfiguration.getDspCallbackAddress();
//        val controller = new CustomMessageReceiverController(identityService, callbackAddress);
//        webService.registerResource(managementApiConfiguration.getContextAlias(), controller);
    }
}
