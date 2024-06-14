package de.sovity.edc.extension.sovitymessenger.demo

import de.sovity.edc.extension.custommessages.api.MessageHandlerRegistry
import de.sovity.edc.extension.custommessages.api.SovityMessenger
import de.sovity.edc.extension.custommessages.api.SovityMessage
import org.eclipse.edc.spi.response.StatusResult
import java.util.concurrent.CompletableFuture
import java.util.function.Function

inline fun <reified IN, OUT> MessageHandlerRegistry.registerKt(crossinline action: (IN) -> OUT)
    where IN : SovityMessage {
    this.register(IN::class.java, Function<IN, OUT> { action(it) })
}

inline fun <reified OUT> SovityMessenger.send(
    counterPartyAddress: String,
    payload: SovityMessage
): CompletableFuture<StatusResult<OUT>>
    where OUT : SovityMessage {
    return this.send(OUT::class.java, counterPartyAddress, payload)
}
