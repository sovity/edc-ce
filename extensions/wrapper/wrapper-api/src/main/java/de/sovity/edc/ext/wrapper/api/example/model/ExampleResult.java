package de.sovity.edc.ext.wrapper.api.example.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "This is the result of the example endpoint.")
public class ExampleResult {
    @Schema(description = "Some name field", required = true)
    private String name;

    @Schema(description = "Some nested object. This documentation should disappear in favor of the " +
            "documentation of TestItem due to constraints of the OpenAPI file format.", required = true)
    private ExampleItem myNestedItem;

    @Schema(description = "Some array of nested objects", required = true)
    private List<ExampleItem> myNestedList;

    @Schema(description = "Configured IDS Endpoint", required = true)
    private String idsEndpoint;
}

