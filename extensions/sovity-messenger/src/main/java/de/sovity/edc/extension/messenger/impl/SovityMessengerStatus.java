package de.sovity.edc.extension.messenger.impl;

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
