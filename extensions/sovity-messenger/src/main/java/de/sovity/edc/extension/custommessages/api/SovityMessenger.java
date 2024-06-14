package de.sovity.edc.extension.custommessages.api;

import org.eclipse.edc.spi.response.StatusResult;

import java.util.concurrent.CompletableFuture;

/**
 * The service to send {@link SovityMessage}s.
 */
public interface SovityMessenger {
    /**
     * Sends a message to the counterparty address and returns a future result.
     *
     * @param resultType          The result's class.
     * @param counterPartyAddress The base DSP URL where to send the message. e.g. https://server:port/api/dsp
     * @param payload             The message to send.
     * @param <T>                 The outgoing message type.
     * @param <R>                 The incoming message type.
     * @return A future result.
     * @throws SovityMessengerException If a problem related to the message processing happened.
     */
    <T extends SovityMessage, R extends SovityMessage>
    CompletableFuture<StatusResult<R>> send(Class<R> resultType, String counterPartyAddress, T payload)
        throws SovityMessengerException;
}
