package de.sovity.edc.extension.messenger.sample;

import de.sovity.edc.extension.messenger.impl.SovityMessengerImpl;
import lombok.val;

import java.util.Map;
import java.util.concurrent.ExecutionException;

class C {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        val o = new SovityMessengerImpl(null, null);

        SampleMessage sample = new SampleMessage(
            "Hello sovity messaging!",
            Map.of(
                "Cabbage", 1,
                "Lettuce", 2));

        val future = o.send(SampleResponse.class, "http://example.com/foo", sample);
        val result = future.get();
        result.onSuccess(it -> System.out.println(it));
    }
}

