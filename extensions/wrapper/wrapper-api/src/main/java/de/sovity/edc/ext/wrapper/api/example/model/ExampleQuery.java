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
public class ExampleQuery {
    private String name;
    private List<String> list;
}
