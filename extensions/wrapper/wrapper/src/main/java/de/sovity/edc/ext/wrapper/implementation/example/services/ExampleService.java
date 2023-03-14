package de.sovity.edc.ext.wrapper.implementation.example.services;

import de.sovity.edc.ext.wrapper.api.example.model.ExampleItem;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ExampleService {
    private final IdsEndpointService idsEndpointService;

    public ExampleResult example(@NonNull ExampleQuery query) {
        requireNonNull(query.getName(), "name must not be null");
        Validate.notEmpty(query.getList(), "list must not be empty");

        var exampleResult = new ExampleResult();
        exampleResult.setName(query.getName());
        exampleResult.setItem(new ExampleItem("example"));
        exampleResult.setList(query.getList().stream().map(ExampleItem::new).toList());
        exampleResult.setIdsEndpoint(idsEndpointService.getIdsEndpoint());
        return exampleResult;
    }
}
