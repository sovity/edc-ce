package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String action;

    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ConstraintDto constraintDto;
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ConstraintDto> andConstraintDtos;
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ConstraintDto> orConstraintDtos;
}
