package de.sovity.edc.extension.custommessages.impl;

import de.sovity.edc.extension.custommessages.api.MessageHandlerRegistry;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class MessageHandlerRegistryImplTest {

    @Test
    void canRegisterAndRetrieveHandler() {
        // arrange
        MessageHandlerRegistry handlers = new MessageHandlerRegistryImpl();
        Function<Integer, String> handler = String::valueOf;

        // act
        handlers.register("itoa", handler);
        val back = handlers.getHandler("itoa");

        // assert
        assertThat(back.handler().apply(1)).isEqualTo("1");
    }
}
