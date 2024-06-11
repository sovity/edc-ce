package de.sovity.edc.extension.custommessages.echo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import okhttp3.Response;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpDispatcherDelegate;
import org.eclipse.edc.spi.EdcException;

import java.io.IOException;
import java.util.function.Function;

public class EchoDispatcherDelegate extends DspHttpDispatcherDelegate<EchoMessage, EchoMessage.Response> {
    @Override
    protected Function<Response, EchoMessage.Response> parseResponse() {
        return res -> {
            try {
                val body = res.body();
                if (body == null) {
                    return null;
                }
                return new ObjectMapper().readValue(body.string(), EchoMessage.Response.class);
            } catch (IOException e) {
                throw new EdcException(e);
            }
        };
    }
}
