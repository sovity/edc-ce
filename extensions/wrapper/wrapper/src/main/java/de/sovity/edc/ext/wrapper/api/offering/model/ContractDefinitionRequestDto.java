//package de.sovity.edc.ext.wrapper.api.offering.model;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.AssertTrue;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
//import org.eclipse.edc.api.model.CriterionDto;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//
//@JsonDeserialize(builder = ContractDefinitionRequestDto.Builder.class)
//@Schema(description = "Test")
//public class ContractDefinitionRequestDto {
//
//    /**
//     * Default validity is set to one year.
//     */
//    private static final long DEFAULT_VALIDITY = TimeUnit.DAYS.toSeconds(365);
//
//    private String id;
//    @NotNull(message = "accessPolicyId cannot be null")
//    private String accessPolicyId;
//    @NotNull(message = "contractPolicyId cannot be null")
//    private String contractPolicyId;
//    @Valid
//    @NotNull(message = "criteria cannot be null")
//    private List<CriterionDto> criteria = new ArrayList<>();
//    @Positive(message = "validity must be positive")
//    private long validity = DEFAULT_VALIDITY;
//
//    private ContractDefinitionRequestDto() {
//    }
//
//    @AssertTrue(message = "id must be either be null or not blank, and it cannot contain the ':' character")
//    @JsonIgnore
//    public boolean isIdValid() {
//        return Optional.of(this)
//                .map(it -> it.id)
//                .map(it -> !it.isBlank() && !it.contains(":"))
//                .orElse(true);
//    }
//
//    public String getAccessPolicyId() {
//        return accessPolicyId;
//    }
//
//    public String getContractPolicyId() {
//        return contractPolicyId;
//    }
//
//    public List<CriterionDto> getCriteria() {
//        return criteria;
//    }
//
//    public long getValidity() {
//        return validity;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    @JsonPOJOBuilder(withPrefix = "")
//    public static final class Builder {
//        private final ContractDefinitionRequestDto dto;
//
//        private Builder() {
//            this.dto = new ContractDefinitionRequestDto();
//        }
//
//        @JsonCreator
//        public static ContractDefinitionRequestDto.Builder newInstance() {
//            return new ContractDefinitionRequestDto.Builder();
//        }
//
//        public ContractDefinitionRequestDto.Builder accessPolicyId(String accessPolicyId) {
//            dto.accessPolicyId = accessPolicyId;
//            return this;
//        }
//
//        public ContractDefinitionRequestDto.Builder contractPolicyId(String contractPolicyId) {
//            dto.contractPolicyId = contractPolicyId;
//            return this;
//        }
//
//        public ContractDefinitionRequestDto.Builder criteria(List<CriterionDto> criteria) {
//            dto.criteria = criteria;
//            return this;
//        }
//
//        public ContractDefinitionRequestDto.Builder validity(long validity) {
//            dto.validity = validity;
//            return this;
//        }
//
//        public ContractDefinitionRequestDto.Builder id(String id) {
//            dto.id = id;
//            return this;
//        }
//
//        public ContractDefinitionRequestDto build() {
//            return dto;
//        }
//    }
//}
//
