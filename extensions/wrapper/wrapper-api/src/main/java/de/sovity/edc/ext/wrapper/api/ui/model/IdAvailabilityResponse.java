package de.sovity.edc.ext.wrapper.api.ui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Object indicates whether an ID for the given object type is already taken or not")
public class IdAvailabilityResponse {
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String id;

    @Schema(description = "ID Availability", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean isAvailable;
}

