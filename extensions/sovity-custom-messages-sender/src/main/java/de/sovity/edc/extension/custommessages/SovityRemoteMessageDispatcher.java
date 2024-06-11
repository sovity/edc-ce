package de.sovity.edc.extension.custommessages;

import org.eclipse.edc.spi.message.RemoteMessageDispatcher;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.util.concurrent.CompletableFuture;

public class SovityRemoteMessageDispatcher implements RemoteMessageDispatcher {

    @Override
    public String protocol() {
        return SovityExtendedProtocol.SOVITY_EXTENDED_PROTOCOL;
    }

    @Override
    public <T, M extends RemoteMessage> CompletableFuture<StatusResult<T>> dispatch(Class<T> responseType, M message) {
        return null;
    }
}
