package de.sovity.edc.ext.wrapper.implementation.example;

import de.sovity.edc.ext.wrapper.api.example.ExampleResource;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExampleResourceImpl implements ExampleResource {

    private final ExampleApiService exampleApiService;

    @Override
    public ExampleResult exampleEndpoint(ExampleQuery query) {
        return exampleApiService.exampleEndpoint(query);
    }
}
