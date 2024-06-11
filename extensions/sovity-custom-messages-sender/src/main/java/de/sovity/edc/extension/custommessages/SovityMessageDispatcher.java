package de.sovity.edc.extension.custommessages;

import okhttp3.Request;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpDispatcherDelegate;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRequestFactory;
import org.eclipse.edc.runtime.metamodel.annotation.ExtensionPoint;
import org.eclipse.edc.spi.message.RemoteMessageDispatcher;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@ExtensionPoint
public interface SovityMessageDispatcher extends RemoteMessageDispatcher {

    <T, M extends RemoteMessage> CompletableFuture<StatusResult<T>> dispatch(Class<T> responseType, @NotNull M message);

    <M extends RemoteMessage, R> void registerMessage(Class<M> clazz, DspHttpRequestFactory<M> requestFactory, DspHttpDispatcherDelegate<M, R> delegate);
}
