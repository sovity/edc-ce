package de.sovity.edc.ext.wrapper.api.ui.pages.data_offer;

import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.DataOfferCreationRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.IdAvailabilityResponse;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.model.PolicyDefinitionCreateDto;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterion;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteral;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteralType;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionOperator;
import de.sovity.edc.ext.wrapper.api.ui.pages.asset.AssetApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_definitions.ContractDefinitionApiService;
import de.sovity.edc.ext.wrapper.api.ui.pages.policy.PolicyDefinitionApiService;
import de.sovity.edc.extension.policy.AlwaysTruePolicyConstants;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class DataOfferPageApiService {

    private final AssetApiService assetApiService;
    private final ContractDefinitionApiService contractDefinitionApiService;
    private final PolicyDefinitionApiService policyDefinitionApiService;

    @NotNull
    public IdAvailabilityResponse checkIfPolicyIdAvailable(DSLContext dsl, String id) {
        val table = Tables.EDC_POLICYDEFINITIONS;
        val field = table.POLICY_ID;

        return new IdAvailabilityResponse(id, isIdAvailable(dsl, table, field, id));
    }

    @NotNull
    public IdAvailabilityResponse checkIfAssetIdAvailable(DSLContext dsl, String id) {
        val table = Tables.EDC_ASSET;
        val field = table.ASSET_ID;

        return new IdAvailabilityResponse(id, isIdAvailable(dsl, table, field, id));
    }

    @NotNull
    public IdAvailabilityResponse checkIfContractDefinitionIdAvailable(DSLContext dsl, String id) {
        val table = Tables.EDC_CONTRACT_DEFINITIONS;
        val field = table.CONTRACT_DEFINITION_ID;

        return new IdAvailabilityResponse(id, isIdAvailable(dsl, table, field, id));
    }

    private boolean isIdAvailable(DSLContext dsl, Table<?> table, TableField<?, String> idField, String id) {
        return !dsl.fetchExists(
            dsl.select(idField)
                .from(table)
                .where(idField.eq(id))
        );
    }

    public IdResponseDto createDataOffer(DSLContext dsl, DataOfferCreationRequest dataOfferCreationRequest) {

        val commonId = dataOfferCreationRequest.getUiAssetCreateRequest().getId();

        return switch (dataOfferCreationRequest.getPolicy()) {
            case DONT_PUBLISH -> createButDontPublish(dsl, dataOfferCreationRequest, commonId);
            case PUBLISH_UNRESTRICTED -> createAndPublishUnrestricted(dsl, dataOfferCreationRequest, commonId);
            case PUBLISH_RESTRICTED -> createAndPublishRestricted(dsl, dataOfferCreationRequest, commonId);
        };
    }

    private @NotNull IdResponseDto createAndPublishUnrestricted(DSLContext dsl, DataOfferCreationRequest dataOfferCreationRequest, String commonId) {
        checkAssetIdAvailable(dsl, commonId);
        checkContractDefinitionIdAvailable(dsl, commonId);
        val policyId = AlwaysTruePolicyConstants.POLICY_DEFINITION_ID;

        assetApiService.createAsset(dataOfferCreationRequest.getUiAssetCreateRequest());

        return createContractDefinition(commonId, policyId, commonId);
    }

    private @NotNull IdResponseDto createAndPublishRestricted(DSLContext dsl, DataOfferCreationRequest dataOfferCreationRequest, String commonId) {
        checkAssetIdAvailable(dsl, commonId);
        checkPolicyIdAvailable(dsl, commonId);
        checkContractDefinitionIdAvailable(dsl, commonId);

        assetApiService.createAsset(dataOfferCreationRequest.getUiAssetCreateRequest());

        val maybeNewPolicy = Optional.ofNullable(dataOfferCreationRequest.getUiPolicyExpression());

        maybeNewPolicy.ifPresent(
            policy -> policyDefinitionApiService.createPolicyDefinitionV2(new PolicyDefinitionCreateDto(commonId, policy)));

        createContractDefinition(commonId, commonId, commonId);

        return new IdResponseDto(commonId, OffsetDateTime.now());
    }

    private @NotNull IdResponseDto createButDontPublish(DSLContext dsl, DataOfferCreationRequest dataOfferCreationRequest, String commonId) {
        checkAssetIdAvailable(dsl, commonId);
        return assetApiService.createAsset(dataOfferCreationRequest.getUiAssetCreateRequest());
    }

    private void checkContractDefinitionIdAvailable(DSLContext dsl, String commonId) {
        val contractDefinitionIdExists = checkIfContractDefinitionIdAvailable(dsl, commonId).isAvailable();
        if (!contractDefinitionIdExists) {
            throw new InvalidRequestException("Contract definition with id %s already exists".formatted(commonId));
        }
    }

    private void checkPolicyIdAvailable(DSLContext dsl, String commonId) {
        val policyIdExists = checkIfPolicyIdAvailable(dsl, commonId).isAvailable();
        if (!policyIdExists) {
            throw new InvalidRequestException("Policy with id %s already exists".formatted(commonId));
        }
    }

    private void checkAssetIdAvailable(DSLContext dsl, String commonId) {
        val assetIdExists = checkIfAssetIdAvailable(dsl, commonId).isAvailable();
        if (!assetIdExists) {
            throw new InvalidRequestException("Asset with id %s already exists".formatted(commonId));
        }
    }

    private @NotNull IdResponseDto createContractDefinition(String assetId, String policyId, String contractDefinitionId) {
        val cd = new ContractDefinitionRequest();
        cd.setAssetSelector(List.of(UiCriterion.builder()
            .operandLeft(Asset.PROPERTY_ID)
            .operator(UiCriterionOperator.EQ)
            .operandRight(UiCriterionLiteral.builder()
                .type(UiCriterionLiteralType.VALUE)
                .value(assetId)
                .build())
            .build()));
        cd.setAccessPolicyId(policyId);
        cd.setContractPolicyId(policyId);
        cd.setContractDefinitionId(contractDefinitionId);

        return contractDefinitionApiService.createContractDefinition(cd);
    }
}
