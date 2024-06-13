package de.sovity.edc.extension.custommessages.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.extension.custommessages.echo.SovityMessageRecord;
import lombok.RequiredArgsConstructor;
import lombok.val;
import okhttp3.Response;
import org.eclipse.edc.protocol.dsp.spi.dispatcher.DspHttpDispatcherDelegate;
import org.eclipse.edc.spi.EdcException;

import java.io.IOException;
import java.util.function.Function;

@RequiredArgsConstructor
public class MessageReceiver extends DspHttpDispatcherDelegate<SovityMessageRecord, SovityMessageRecord> {

    private final ObjectMapper mapper;

    @Override
    protected Function<Response, SovityMessageRecord> parseResponse() {
        return res -> {
            try {
                val body = res.body();
                if (body == null) {
                    return null;
                }
                String content = body.string();
                return mapper.readValue(content, SovityMessageRecord.class);
            } catch (IOException e) {
                throw new EdcException(e);
            }
        };
    }
}
