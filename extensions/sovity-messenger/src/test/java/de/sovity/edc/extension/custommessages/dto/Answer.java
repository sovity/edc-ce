package de.sovity.edc.extension.custommessages.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.custommessages.api.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// TODO: rename package to sovity messenger

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Answer implements SovityMessage {
    @Override
    public String getType() {
        return getClass().getCanonicalName();
    }

    @JsonProperty("answer")
    private int answer;
}
