package de.sovity.edc.ext.wrapper.implementation.example;

import de.sovity.edc.ext.wrapper.api.example.ExampleResource;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import de.sovity.edc.ext.wrapper.implementation.example.services.ExampleService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExampleResourceImpl implements ExampleResource {

    private final ExampleService exampleService;

    @Override
    public ExampleResult example(ExampleQuery query) {
        return exampleService.example(query);
    }
}
