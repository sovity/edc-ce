package de.sovity.edc.extension.custommessages.sample;

import de.sovity.edc.extension.custommessages.api.SovityMessage;
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
