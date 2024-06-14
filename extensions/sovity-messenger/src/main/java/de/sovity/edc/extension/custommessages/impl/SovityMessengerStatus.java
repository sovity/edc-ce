package de.sovity.edc.extension.custommessages.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SovityMessengerStatus {

    NO_HANDLER("no_handler"),
    OK("ok"),
    ;

    private String code;
}
