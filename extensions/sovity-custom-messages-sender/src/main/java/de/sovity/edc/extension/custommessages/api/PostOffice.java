package de.sovity.edc.extension.custommessages.api;

import org.eclipse.edc.spi.response.StatusResult;

import java.util.concurrent.CompletableFuture;

public interface PostOffice {
    <T extends SovityMessage, R extends SovityMessage>
    CompletableFuture<StatusResult<R>> send(Class<R> resultType, String counterPartyAddress, T payload);
}
