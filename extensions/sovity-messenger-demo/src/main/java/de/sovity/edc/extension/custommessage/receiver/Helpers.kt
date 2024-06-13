package de.sovity.edc.extension.custommessage.receiver

import de.sovity.edc.extension.custommessages.api.MessageHandlerRegistry
import de.sovity.edc.extension.custommessages.api.PostOffice
import de.sovity.edc.extension.custommessages.api.SovityMessage
import org.eclipse.edc.spi.response.StatusResult
import java.util.concurrent.CompletableFuture
import java.util.function.Function

inline fun <reified IN, OUT> MessageHandlerRegistry.registerKt(crossinline action: (IN) -> OUT)
    where IN : SovityMessage {
    this.register(IN::class.java, Function<IN, OUT> { action(it) })
}

inline fun <reified OUT> PostOffice.send(
    counterPartyAddress: String,
    payload: SovityMessage
): CompletableFuture<StatusResult<OUT>>
    where OUT : SovityMessage {
    return this.send(OUT::class.java, counterPartyAddress, payload)
}
