package de.sovity.edc.extension.custommessages.api;

import org.eclipse.edc.spi.response.StatusResult;

import java.util.concurrent.CompletableFuture;

public interface SovityExchange<OUT extends SovityMessage, IN extends SovityMessage> {
    CompletableFuture<StatusResult<IN>> send(OUT payload);
}
