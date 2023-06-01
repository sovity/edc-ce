package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Subset of the possible permissions in the EDC.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PermissionDto {

    @Schema(description = "Possible constraints for the permission",
            requiredMode = RequiredMode.REQUIRED)
    private ExpressionDto constraints;
}
