package de.sovity.edc.ext.wrapper.api.common.model;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
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

    /**
     * Builder for PermissionDto.
     */
    public static class Builder {

        /** The DTO to build. */
        private final PermissionDto permissionDto;

        private Builder() {
            this.permissionDto = new PermissionDto();
        }

        public static Builder newInstance() {
            return new Builder();
        }

        /**
         * Sets the permission's action.
         *
         * @param action the action
         * @return the builder
         */
        public Builder action(String action) {
            this.permissionDto.action = action;
            return this;
        }

        /**
         * Sets the permission's constraint.
         *
         * @param constraintDto the constraint
         * @return the builder
         */
        public Builder constraint(ConstraintDto constraintDto) {
            this.permissionDto.constraintDto = constraintDto;
            return this;
        }

        /**
         * Sets a list of constraints for the permission that all need to be fulfilled.
         *
         * @param constraintDtos the constraints
         * @return the builder
         */
        public Builder andConstraint(List<ConstraintDto> constraintDtos) {
            this.permissionDto.andConstraintDtos = constraintDtos;
            return this;
        }

        /**
         * Sets a list of constraints for the permission of which one needs to be fulfilled.
         *
         * @param constraintDtos the constraints
         * @return the builder
         */
        public Builder orConstraint(List<ConstraintDto> constraintDtos) {
            this.permissionDto.orConstraintDtos = constraintDtos;
            return this;
        }

        /**
         * Returns the DTO.
         *
         * @return the DTO
         */
        public PermissionDto build() {
            Objects.requireNonNull(this.permissionDto.action);
            return permissionDto;
        }
    }
}
