package de.sovity.edc.ext.wrapper.api.example.model;

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
public class ExampleResult {
    private String name;
    private ExampleItem item;
    private List<ExampleItem> list;
    private String idsEndpoint;
}

