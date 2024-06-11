package de.sovity.edc.extension.custommessages;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.event.Event;
import org.eclipse.edc.spi.event.EventEnvelope;
import org.eclipse.edc.spi.event.EventSubscriber;
import org.eclipse.edc.spi.monitor.Monitor;

@RequiredArgsConstructor
public class SovityEventSubscriber implements EventSubscriber {

    private final Monitor monitor;

    @Override
    public <E extends Event> void on(EventEnvelope<E> event) {
        monitor.debug(() -> "on EventEnvelope " + event.getId());
    }
}
