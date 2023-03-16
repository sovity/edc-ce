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
@Schema(description = "This is the query of the example endpoint.")
public class ExampleQuery {

    @Schema(required = true)
    private String name;

    @Schema(required = true)
    private List<String> list;
}
