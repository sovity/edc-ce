package de.sovity.edc.extension.sovitymessenger.demo

import de.sovity.edc.extension.messenger.api.MessageHandlerRegistry
import de.sovity.edc.extension.messenger.api.SovityMessenger
import de.sovity.edc.extension.sovitymessenger.demo.message.Addition
import de.sovity.edc.extension.sovitymessenger.demo.message.Answer
import de.sovity.edc.extension.sovitymessenger.demo.message.Sqrt
import org.eclipse.edc.runtime.metamodel.annotation.Inject
import org.eclipse.edc.spi.system.ServiceExtensionContext
import kotlin.math.sqrt

class SovityMessengerDemoKt /* : ServiceExtension */ {


    @Inject
    private lateinit var sovityMessenger: SovityMessenger

    @Inject
    private lateinit var registry: MessageHandlerRegistry

    /* override */ fun initialize(context: ServiceExtensionContext) {
        /**
         * Less verbose and more typesafe with a better language.
         */
        registry.registerKt { it: Sqrt -> Answer(sqrt(it.a)) }
        registry.registerKt { it: Addition -> Answer((it.a + it.b).toDouble()) }
    }

    companion object {
        const val NAME: String = "sovityMessengerDemo"
    }
}
