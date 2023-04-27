//package de.sovity.edc.ext.wrapper.api.offering.model;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//
//import java.util.Objects;
//
//@Getter
//@Setter
//@ToString
//@AllArgsConstructor
//@RequiredArgsConstructor
//@Schema(description = "Test")
//public class PolicyDefinitionRequestDto {
//
//    private String id;
//    @NotNull
//    private Policy policy;
//
//    private final int test = 0;
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(Objects.hash(id), policy.hashCode());
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        PolicyDefinitionRequestDto that = (PolicyDefinitionRequestDto) o;
//        return Objects.equals(id, that.id) && policy.equals(that.policy);
//    }
//
//}
