package de.sovity.edc.ext.wrapper.implementation.example;

import de.sovity.edc.ext.wrapper.api.example.model.ExampleItem;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleQuery;
import de.sovity.edc.ext.wrapper.api.example.model.ExampleResult;
import de.sovity.edc.ext.wrapper.implementation.example.services.IdsEndpointService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.Validate;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class ExampleApiService {
    private final IdsEndpointService idsEndpointService;

    public ExampleResult exampleEndpoint(@NonNull ExampleQuery query) {
        requireNonNull(query.getName(), "name must not be null");
        Validate.notEmpty(query.getList(), "list must not be empty");

        var testResult = new ExampleResult();
        testResult.setName(query.getName());
        testResult.setMyNestedItem(new ExampleItem("test"));
        testResult.setMyNestedList(query.getList().stream().map(ExampleItem::new).toList());
        testResult.setIdsEndpoint(idsEndpointService.getIdsEndpoint());
        return testResult;
    }
}
