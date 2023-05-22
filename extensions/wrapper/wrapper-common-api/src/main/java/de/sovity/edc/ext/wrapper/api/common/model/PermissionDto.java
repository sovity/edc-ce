package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PermissionDto {
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    private String action;

    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ConstraintDto constraintDto;
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ConstraintDto> andConstraintDtos;
    @Schema(description = "ToDo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ConstraintDto> orConstraintDtos;
}
