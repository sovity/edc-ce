package de.sovity.edc.extension.custommessages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.custommessages.echo.EchoMessage;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.spi.EdcException;

import java.util.function.Function;

public class DummyDspHttpRequestFactory implements Function<EchoMessage, Request> {

    @Override
    public Request apply(EchoMessage dummy) {
        try {
            return new Request.Builder()
                .post(RequestBody.create(
                    new ObjectMapper().writeValueAsString(dummy),
                    MediaType.get(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                ))
                .build();
        } catch (JsonProcessingException e) {
            throw new EdcException(e);
        }
    }
}
