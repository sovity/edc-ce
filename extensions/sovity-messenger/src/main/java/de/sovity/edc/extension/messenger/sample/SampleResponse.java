package de.sovity.edc.extension.messenger.sample;

import de.sovity.edc.extension.messenger.api.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SampleResponse implements SovityMessage {

    private String bill;

    @Override
    public String getType() {
        return "sampleResponse";
    }
}


