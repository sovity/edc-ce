package de.sovity.edc.ext.wrapper.api.usecase.transformer;

import de.sovity.edc.ext.wrapper.api.common.model.ExpressionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractNegotiationOutputDto;
import org.eclipse.edc.connector.contract.spi.types.negotiation.ContractNegotiation;
import org.eclipse.edc.policy.model.*;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PermissionToPermissionDtoTransformerTest {

    private final PermissionToPermissionDtoTransformer transformer = new PermissionToPermissionDtoTransformer();
    private final TransformerContext context = mock(TransformerContext.class);

    @Test
    void types() {
        assertThat(transformer.getInputType()).isEqualTo(Permission.class);
        assertThat(transformer.getOutputType()).isEqualTo(PermissionDto.class);
    }

    @Test
    void transform(){
        var atomicConstraint = AtomicConstraint.Builder.newInstance().rightExpression(new LiteralExpression("eu")).leftExpression(new LiteralExpression("absoluteSpatialPosition")).build();
        var permission = Permission.Builder.newInstance().action(Action.Builder.newInstance().type("USE").build()).constraint(atomicConstraint).build();

        var result = transformer.transform(permission,context);

        assertThat(result).isNotNull();
        assertThat(result.getConstraints()).isNotNull();


    }
}
