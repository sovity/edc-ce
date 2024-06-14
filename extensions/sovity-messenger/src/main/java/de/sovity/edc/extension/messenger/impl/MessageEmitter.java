package de.sovity.edc.extension.messenger.impl;

import de.sovity.edc.extension.messenger.api.SovityMessageApi;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRequestFactory;
import org.eclipse.edc.protocol.dsp.spi.serialization.JsonLdRemoteMessageSerializer;

@RequiredArgsConstructor
public class MessageEmitter implements DspHttpRequestFactory<SovityMessageRequest> {

    private final JsonLdRemoteMessageSerializer serializer;

    @Override
    public Request createRequest(SovityMessageRequest message) {
        String serialized = serializer.serialize(message);
        return new Request.Builder()
            .url(message.counterPartyAddress() + SovityMessageApi.PATH)
            .post(RequestBody.create(
                serialized,
                MediaType.get(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
            ))
            .build();
    }
}
