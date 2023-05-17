//package de.sovity.edc.ext.wrapper.api.offering.model;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//
//@JsonDeserialize(builder = AssetEntryDto.Builder.class)
//@Schema(description = "Test")
//public class AssetEntryDto {
//    @NotNull(message = "asset cannot be null")
//    @Valid
//    private AssetRequestDto assetTest;
//    @NotNull(message = "dataAddress cannot be null")
//    @Valid
//    private DataAddressDto dataAddress;
//
//
//    private AssetEntryDto() {
//    }
//
//    public AssetRequestDto getAsset() {
//        return assetTest;
//    }
//
//    public DataAddressDto getDataAddress() {
//        return dataAddress;
//    }
//
//    @JsonPOJOBuilder(withPrefix = "")
//    public static final class Builder {
//
//        private final AssetEntryDto assetEntryDto;
//
//        private Builder() {
//            assetEntryDto = new AssetEntryDto();
//        }
//
//        @JsonCreator
//        public static AssetEntryDto.Builder newInstance() {
//            return new AssetEntryDto.Builder();
//        }
//
//        public AssetEntryDto.Builder asset(AssetRequestDto asset) {
//            assetEntryDto.assetTest = asset;
//            return this;
//        }
//
//        public AssetEntryDto.Builder dataAddress(DataAddressDto dataAddress) {
//            assetEntryDto.dataAddress = dataAddress;
//            return this;
//        }
//
//        public AssetEntryDto build() {
//            return assetEntryDto;
//        }
//    }
//}
