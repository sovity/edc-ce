package de.sovity.edc.extension.custommessages.impl;

import de.sovity.edc.extension.custommessages.api.GenericMessageHandler;
import de.sovity.edc.extension.custommessages.api.MessageHandlers;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MessageHandlersImplTest {

    @Test
    void canRegisterAndRetrieveHandler() {
        // arrange
        MessageHandlers handlers = new MessageHandlersImpl();
        Function<Integer, String> handler = String::valueOf;

        // act
        handlers.register("itoa", handler);
        val back = handlers.getHandler("itoa");

        // assert
        assertThat(back.handler().handle(1)).isEqualTo("1");
    }
}
