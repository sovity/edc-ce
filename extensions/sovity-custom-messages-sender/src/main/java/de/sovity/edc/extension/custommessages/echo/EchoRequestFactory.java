package de.sovity.edc.extension.custommessages.echo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpRequestFactory;
import org.eclipse.edc.spi.EdcException;

public class EchoRequestFactory implements DspHttpRequestFactory<EchoMessage> {
    @Override
    public Request createRequest(EchoMessage message) {
        try {
            return new Request.Builder()
                .url(message.counterPartyAddress() + "/sovity/message/echo")
                .post(RequestBody.create(
                    new ObjectMapper().writeValueAsString(message),
                    MediaType.get(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                ))
                .build();
        } catch (JsonProcessingException e) {
            throw new EdcException(e);
        }
    }
}
