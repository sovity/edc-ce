package de.sovity.edc.extension.sovitymessenger.demo.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.api.SovityMessage;

public class Answer implements SovityMessage {

    public Answer() {
    }

    public Answer(double answer) {
        this.answer = answer;
    }

    @Override
    public String getType() {
        return Common.ROOT + "answer";
    }

    @JsonProperty
    private double answer;

    public double getAnswer() {
        return answer;
    }
}
