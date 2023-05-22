package de.sovity.edc.ext.wrapper.api.common.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Subset of the possible permissions in the EDC.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PermissionDto {

    @Schema(description = "Specifies how the permission for the policy must be applied",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NonNull
    private String action;

    @Schema(description = "Possible single constraint for the permission",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ConstraintDto constraintDto;
    @Schema(description = "Several constraints, all of which must be respected",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ConstraintDto> andConstraintDtos;
    @Schema(description = "Several constraints, of which at least one must be respected",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ConstraintDto> orConstraintDtos;
}
