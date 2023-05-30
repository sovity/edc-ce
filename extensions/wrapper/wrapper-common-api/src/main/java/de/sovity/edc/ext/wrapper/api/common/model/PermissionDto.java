package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
@Builder(toBuilder = true)
public class PermissionDto {

    @Schema(description = "Possible single constraint for the permission",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ConstraintDto constraint;
    @Schema(description = "Several constraints, all of which must be respected",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ConstraintDto> andConstraint;
    @Schema(description = "Several constraints, of which at least one must be respected",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ConstraintDto> orConstraint;
}
