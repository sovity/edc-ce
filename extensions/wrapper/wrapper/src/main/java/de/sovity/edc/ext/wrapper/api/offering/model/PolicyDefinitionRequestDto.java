package de.sovity.edc.ext.wrapper.api.offering.model;

import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "ToDo")
public class PolicyDefinitionRequestDto {
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.REQUIRED)
    private PolicyDto policy;
}
