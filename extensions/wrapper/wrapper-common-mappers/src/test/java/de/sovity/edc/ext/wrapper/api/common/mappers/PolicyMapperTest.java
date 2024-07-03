package de.sovity.edc.ext.wrapper.api.common.mappers;

//import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.common.mappers.utils.*;
import de.sovity.edc.ext.wrapper.api.common.model.*;
import de.sovity.edc.ext.wrapper.api.common.model.Expression;
import jakarta.json.JsonObject;
import lombok.SneakyThrows;
import org.eclipse.edc.policy.model.*;
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

import static de.sovity.edc.ext.wrapper.api.common.model.ExpressionType.ATOMIC_CONSTRAINT;
import static de.sovity.edc.utils.JsonUtils.parseJsonObj;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
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
        var expressionType = ExpressionType.valueOf(constraintTypeString);
        var incomingConstraint = mock(AtomicConstraintDto.class);
        var mappedAtomicConstraint = mock(AtomicConstraint.class);
        var atomicConstraint = new Expression(ATOMIC_CONSTRAINT, List.of(), incomingConstraint);
        var atomicConstraints = List.of(atomicConstraint, atomicConstraint);
        var baseConstraintElement = new Expression(expressionType, atomicConstraints, null);

        // act
        when(atomicConstraintMapper
            .buildAtomicConstraint(eq(incomingConstraint)))
            .thenReturn(mappedAtomicConstraint);
        var policy = policyMapper.buildPolicy(List.of(baseConstraintElement));

        // assert
        assertThat(policy.getType()).isEqualTo(PolicyType.SET);
        assertThat(policy.getPermissions()).hasSize(1);
        var permission = policy.getPermissions().get(0);
        assertThat(permission.getConstraints()).hasSize(1);
        assertThat(permission.getAction().getType()).isEqualTo("USE");

        var constraintObject = permission.getConstraints().get(0);
        assertNotNull(constraintObject);
    }

    @Test
    void testBuildConstraints() throws Exception {

        AtomicConstraintMapper atomicConstraintMapper1 = new AtomicConstraintMapper(new LiteralMapper(new ObjectMapper()), new OperatorMapper());

        var realPolicyMapper = new PolicyMapper(new ConstraintExtractor(new PolicyValidator(), atomicConstraintMapper1), atomicConstraintMapper1, mock(TypeTransformerRegistry.class));

//        String expressionJson = "{ \"expressionType\": \"ATOMIC_CONSTRAINT\", \"atomicConstraint\": { \"leftExpression\": \"REFERRING_CONNECTOR\", \"operator\": \"EQ\", \"rightExpression\":  \"asd\" } }";

        String expressionJson = """
        {
        "expressionType": "AND",
        "leftExpression": {
            "expressionType": "ATOMIC_CONSTRAINT",
            "atomicConstraint": {
                "leftExpression": "REFERRING_CONNECTOR",
                "operator": "EQ",
                "rightExpression":  "asd"
            }
        },
        "rightExpression": {
            "expressionType": "OR",
            "leftExpression": {
                "expressionType": "ATOMIC_CONSTRAINT",
                "atomicConstraint": {
                    "leftExpression": "POLICY_EVALUATION_TIME",
                    "operator": "GEQ",
                    "rightExpression": "2024-06-15T22:00:00.000Z"
                }
            },
            "rightExpression": {
            "expressionType": "ATOMIC_CONSTRAINT",
            "atomicConstraint": {
                "leftExpression": "POLICY_EVALUATION_TIME",
                "operator": "LT",
                "rightExpression": "2024-06-22T22:00:00.000Z"
                    }
                }
            }
        }""";

        ObjectMapper objectMapper = new ObjectMapper();
        MultiExpression expression = objectMapper.readValue(expressionJson, MultiExpression.class);

        var s = """
                { "expressionType": "ATOMIC_CONSTRAINT",
                    "atomicConstraint": {
                        "leftExpression": "REFERRING_CONNECTOR", "operator": "EQ", "rightExpression":  "asd" }
                }
            """;

        var constraints = realPolicyMapper.convertExpressionToConstraints(expression);

        System.out.println("Constraints: " + constraints.size());
        constraints.forEach(System.out::println);


    }

    @SneakyThrows
    @Test
    void testBuildConstraintsMock() {
        /* String expressionJson = "{ \"expressionType\": \"AND\", \"leftExpression\": { \"expressionType\": \"ATOMIC_CONSTRAINT\", \"atomicConstraint\": { \"leftExpression\": \"REFERRING_CONNECTOR\", \"operator\": \"EQ\", \"rightExpression\":  \"asd\" } }, \"rightExpression\": { \"expressionType\": \"OR\", \"leftExpression\": { \"expressionType\": \"ATOMIC_CONSTRAINT\", \"atomicConstraint\": { \"leftExpression\": \"POLICY_EVALUATION_TIME\", \"operator\": \"GEQ\", \"rightExpression\": \"2024-06-15T22:00:00.000Z\" } }, \"rightExpression\": { \"expressionType\": \"ATOMIC_CONSTRAINT\", \"atomicConstraint\": { \"leftExpression\": \"POLICY_EVALUATION_TIME\", \"operator\": \"LT\", \"rightExpression\": \"2024-06-22T22:00:00.000Z\"  } } } }";

         */

        String expressionJson = "{ \"expressionType\": \"ATOMIC_CONSTRAINT\", \"atomicConstraint\": { \"leftExpression\": \"REFERRING_CONNECTOR\", \"operator\": \"EQ\", \"rightExpression\":  \"asd\" } }";

        ObjectMapper objectMapper = new ObjectMapper();
        MultiExpression expression = objectMapper.readValue(expressionJson, MultiExpression.class);

//        List<Constraint> constraints = policyMapper.convertExpressionToConstraints(expression);

        when(atomicConstraintMapper.buildAtomicConstraint(any())).thenReturn(mock(AtomicConstraint.class));

        policyMapper.convertExpressionToConstraints(expression);

//        constraints.forEach(System.out::println);

    }

}


