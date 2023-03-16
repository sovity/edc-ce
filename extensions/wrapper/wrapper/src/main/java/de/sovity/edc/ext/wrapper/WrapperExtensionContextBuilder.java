package de.sovity.edc.ext.wrapper;

import de.sovity.edc.ext.wrapper.implementation.example.ExampleApiService;
import de.sovity.edc.ext.wrapper.implementation.example.ExampleResourceImpl;
import de.sovity.edc.ext.wrapper.implementation.example.services.IdsEndpointService;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.List;

@RequiredArgsConstructor
public class WrapperExtensionContextBuilder {
    private final Config config;

    public WrapperExtensionContext buildContext() {
        var idsEndpointService = new IdsEndpointService(config);
        var exampleApiService = new ExampleApiService(idsEndpointService);
        var exampleResource = new ExampleResourceImpl(exampleApiService);

        // Collect all JAX-RS resources
        return new WrapperExtensionContext(List.of(
                exampleResource
        ));
    }
}
