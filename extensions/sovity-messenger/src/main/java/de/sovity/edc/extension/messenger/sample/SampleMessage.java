package de.sovity.edc.extension.messenger.sample;

import de.sovity.edc.extension.messenger.api.SovityMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter
public class SampleMessage implements SovityMessage {
    private final String type = "sample";

    private final String greeting;
    private final Map<String, Integer> errandsList;
}
