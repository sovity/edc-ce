package de.sovity.edc.extension.custommessages.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.custommessages.api.SovityMessageApi;
import de.sovity.edc.extension.custommessages.echo.SovityMessageRecord;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRequestFactory;
import org.eclipse.edc.protocol.dsp.spi.serialization.JsonLdRemoteMessageSerializer;

@RequiredArgsConstructor
public class MessageEmitter implements DspHttpRequestFactory<SovityMessageRecord> {

    private final JsonLdRemoteMessageSerializer serializer;

    @Override
    public Request createRequest(SovityMessageRecord message) {
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
