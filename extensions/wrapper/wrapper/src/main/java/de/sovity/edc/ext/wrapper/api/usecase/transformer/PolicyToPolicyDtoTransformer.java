package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.edc.transform.spi.TypeTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PolicyToPolicyDtoTransformer implements TypeTransformer<Policy, PolicyDto> {
    @Override
    public Class<Policy> getInputType() {
        return Policy.class;
    }

    @Override
    public Class<PolicyDto> getOutputType() {
        return PolicyDto.class;
    }

    @Override
    public @Nullable PolicyDto transform(@NotNull Policy policy, @NotNull TransformerContext context) {
        if (policy.getPermissions() != null && !policy.getPermissions().isEmpty()){
            var permission = policy.getPermissions().get(0);
            var permissionDto = context.transform(permission, PermissionDto.class);

            if (permissionDto == null){
                context.problem().nullProperty().type(Policy.class).property("Permissions");
                return null;
            }

            return  PolicyDto
                    .builder()
                    .permission(permissionDto)
                    .build();
        }

        context.problem().nullProperty().type(Policy.class).property("Permissions").report();
        return null;

    }
}
