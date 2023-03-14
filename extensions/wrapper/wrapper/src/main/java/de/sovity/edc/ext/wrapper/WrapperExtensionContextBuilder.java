package de.sovity.edc.ext.wrapper;

import de.sovity.edc.ext.wrapper.implementation.example.ExampleResourceImpl;
import de.sovity.edc.ext.wrapper.implementation.example.services.IdsEndpointService;
import de.sovity.edc.ext.wrapper.implementation.example.services.ExampleService;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class WrapperExtensionContextBuilder {
    private final Config config;

    public WrapperExtensionContext buildContext() {
        List<Object> resources = new ArrayList<>();
        var idsEndpointService = new IdsEndpointService(config);
        var exampleService = new ExampleService(idsEndpointService);
        var exampleResource = new ExampleResourceImpl(exampleService);

        resources.add(exampleResource);

        return new WrapperExtensionContext(resources);
    }
}
