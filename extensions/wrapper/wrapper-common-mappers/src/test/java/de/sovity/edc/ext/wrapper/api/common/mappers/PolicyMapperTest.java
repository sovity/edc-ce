package de.sovity.edc.ext.wrapper.api.common.mappers;

import de.sovity.edc.ext.wrapper.api.common.mappers.utils.AtomicConstraintMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.ConstraintExtractor;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.MappingErrors;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraint;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyElement;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraintType;
import de.sovity.edc.ext.wrapper.api.common.model.UiPolicyCreateRequest;
import jakarta.json.JsonObject;
import lombok.SneakyThrows;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.PolicyType;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.transform.spi.TypeTransformerRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static de.sovity.edc.ext.wrapper.api.common.model.UiPolicyConstraintType.ATOMIC;
import static de.sovity.edc.utils.JsonUtils.parseJsonObj;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyMapperTest {
    @InjectMocks
    PolicyMapper policyMapper;

    @Mock
    TypeTransformerRegistry transformerRegistry;

    @Mock
    ConstraintExtractor constraintExtractor;

    @Mock
    AtomicConstraintMapper atomicConstraintMapper;


    @Test
    @SneakyThrows
    void test_buildPolicyDto() {
        try (MockedStatic<MappingErrors> mappingErrors = mockStatic(MappingErrors.class)) {
            // arrange
            var policy = mock(Policy.class);
            var errors = mock(MappingErrors.class);
            var constraints = List.of(mock(UiPolicyConstraint.class));

            when(errors.getErrors()).thenReturn(List.of("error1"));

            mappingErrors.when(MappingErrors::root).thenReturn(errors);
            when(constraintExtractor.getPermissionConstraints(policy, errors)).thenReturn(constraints);
            when(transformerRegistry.transform(policy, JsonObject.class)).thenReturn(Result.success(parseJsonObj("{}")));

            // act
            var actual = policyMapper.buildUiPolicy(policy);

            // assert
            assertThat(actual.getPolicyJsonLd()).isEqualTo("{}");
            assertThat(actual.getConstraints()).isEqualTo(constraints);
            assertThat(actual.getErrors()).isEqualTo(List.of("error1"));
        }
    }

    @Test
    void test_buildPolicy() {
        // arrange
        var constraint = mock(UiPolicyConstraint.class);
        var createRequest = new UiPolicyCreateRequest(List.of(constraint));

        var expected = mock(AtomicConstraint.class);
        when(atomicConstraintMapper.buildAtomicConstraints(eq(List.of(constraint))))
                .thenReturn(List.of(expected));

        // act
        var actual = policyMapper.buildPolicy(createRequest);

        // assert
        assertThat(actual.getType()).isEqualTo(PolicyType.SET);
        assertThat(actual.getPermissions()).hasSize(1);
        assertThat(actual.getPermissions().get(0).getConstraints()).hasSize(1);
        assertThat(actual.getPermissions().get(0).getAction().getType()).isEqualTo("USE");
        assertThat(actual.getPermissions().get(0).getConstraints().get(0)).isSameAs(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"AND", "OR", "XOR"})
    void buildGenericPolicy(String constraintTypeString) {
        // arrange
        var constraintType = UiPolicyConstraintType.valueOf(constraintTypeString);
        var incomingConstraint = mock(UiPolicyConstraint.class);
        var mockAtomicConstraint = mock(AtomicConstraint.class);
        var atomicConstraint = new PolicyElement(ATOMIC, List.of(), incomingConstraint);
        var atomicConstraints = List.of(atomicConstraint, atomicConstraint);
        var baseConstraintElement = new PolicyElement(constraintType, atomicConstraints, null);

        // act
        when(atomicConstraintMapper
                .buildAtomicConstraint(eq(incomingConstraint)))
                .thenReturn(mockAtomicConstraint);
        var policy = policyMapper.buildMultiplicityPolicy(List.of(baseConstraintElement));

        // assert
        assertThat(policy.getType()).isEqualTo(PolicyType.SET);
        assertThat(policy.getPermissions()).hasSize(1);
        var permission = policy.getPermissions().get(0);
        assertThat(permission.getConstraints()).hasSize(1);
        assertThat(permission.getAction().getType()).isEqualTo("USE");

        var constraintObject = permission.getConstraints().get(0);
        assertNotNull(constraintObject);
    }
}
