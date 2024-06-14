package de.sovity.edc.extension.messenger.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import okhttp3.Response;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpDispatcherDelegate;
import org.eclipse.edc.spi.EdcException;

import java.io.IOException;
import java.util.function.Function;

@RequiredArgsConstructor
public class MessageReceiver extends DspHttpDispatcherDelegate<SovityMessageRequest, SovityMessageRequest> {

    private final ObjectMapper mapper;

    @Override
    protected Function<Response, SovityMessageRequest> parseResponse() {
        return res -> {
            try {
                val body = res.body();
                if (body == null) {
                    return null;
                }
                String content = body.string();
                return mapper.readValue(content, SovityMessageRequest.class);
            } catch (IOException e) {
                throw new EdcException(e);
            }
        };
    }
}
