package de.sovity.edc.ext.wrapper.api.offering.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Test")
public class DataAddressDto {

    @NotNull(message = "properties cannot be null")
    private Map<String, String> properties;

    @Schema(description = "int test")
    private int test;

    @JsonIgnore
    @AssertTrue(message = "property keys cannot be blank and property 'type' is mandatory")
    public boolean isValid() {
        return properties != null && properties.keySet().stream().noneMatch(it -> it == null || it.isBlank()) && properties.containsKey("type");
    }
}
