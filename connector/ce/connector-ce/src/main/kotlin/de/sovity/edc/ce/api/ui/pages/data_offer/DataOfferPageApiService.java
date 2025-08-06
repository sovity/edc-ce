/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.data_offer;

import de.sovity.edc.ce.api.ui.model.ContractDefinitionRequest;
import de.sovity.edc.ce.api.ui.model.DataOfferCreateRequest;
import de.sovity.edc.ce.api.ui.model.IdAvailabilityResponse;
import de.sovity.edc.ce.api.ui.model.IdResponseDto;
import de.sovity.edc.ce.api.ui.model.PolicyDefinitionCreateDto;
import de.sovity.edc.ce.api.ui.model.UiCriterion;
import de.sovity.edc.ce.api.ui.model.UiCriterionLiteral;
import de.sovity.edc.ce.api.ui.model.UiCriterionLiteralType;
import de.sovity.edc.ce.api.ui.model.UiCriterionOperator;
import de.sovity.edc.ce.api.ui.pages.asset.AssetApiService;
import de.sovity.edc.ce.api.ui.pages.contract_definitions.ContractDefinitionApiService;
import de.sovity.edc.ce.api.ui.pages.policy.PolicyDefinitionApiService;
import de.sovity.edc.ce.db.jooq.Tables;
import de.sovity.edc.ce.modules.policy_utils.always_true.AlwaysTruePolicyConstants;
import de.sovity.edc.ce.modules.policy_utils.always_true.AlwaysTruePolicyDefinitionService;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DataOfferPageApiService {

    private final AssetApiService assetApiService;
    private final ContractDefinitionApiService contractDefinitionApiService;
    private final PolicyDefinitionApiService policyDefinitionApiService;
    private final AlwaysTruePolicyDefinitionService alwaysTruePolicyDefinitionService;

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

    public IdResponseDto createDataOffer(DSLContext dsl, DataOfferCreateRequest dataOfferCreateRequest) {
        val commonId = dataOfferCreateRequest.getAsset().getId();

        return switch (dataOfferCreateRequest.getPublishType()) {
            case DONT_PUBLISH -> createButDontPublish(dsl, dataOfferCreateRequest, commonId);
            case PUBLISH_UNRESTRICTED -> createAndPublishUnrestricted(dsl, dataOfferCreateRequest, commonId);
            case PUBLISH_RESTRICTED -> createAndPublishRestricted(dsl, dataOfferCreateRequest, commonId);
        };
    }

    private @NotNull IdResponseDto createAndPublishUnrestricted(
        DSLContext dsl,
        DataOfferCreateRequest dataOfferCreateRequest,
        String commonId
    ) {
        val assetId = commonId;
        val contractDefinitionId = commonId;
        val policyId = AlwaysTruePolicyConstants.POLICY_DEFINITION_ID;

        checkAssetIdAvailable(dsl, assetId);
        checkContractDefinitionIdAvailable(dsl, contractDefinitionId);

        if (!alwaysTruePolicyDefinitionService.exists()) {
            // the default always-true policy has been deleted, recreate it.
            alwaysTruePolicyDefinitionService.create();
        }

        assetApiService.createAsset(dsl, dataOfferCreateRequest.getAsset());

        return createContractDefinition(assetId, policyId, contractDefinitionId);
    }

    private @NotNull IdResponseDto createAndPublishRestricted(
        DSLContext dsl,
        DataOfferCreateRequest dataOfferCreateRequest,
        String commonId
    ) {
        val assetId = commonId;
        val policyId = commonId;
        val contractDefinitionId = commonId;

        checkAssetIdAvailable(dsl, assetId);
        checkPolicyIdAvailable(dsl, policyId);
        checkContractDefinitionIdAvailable(dsl, contractDefinitionId);

        assetApiService.createAsset(dsl, dataOfferCreateRequest.getAsset());

        val policyExpression = Optional.ofNullable(dataOfferCreateRequest.getPolicyExpression())
            .orElseThrow(() -> new InvalidRequestException("Missing policy expression"));
        policyDefinitionApiService.createPolicyDefinitionV2(new PolicyDefinitionCreateDto(policyId, policyExpression));

        createContractDefinition(assetId, policyId, contractDefinitionId);

        return new IdResponseDto(commonId, OffsetDateTime.now());
    }

    private @NotNull IdResponseDto createButDontPublish(
        DSLContext dsl,
        DataOfferCreateRequest dataOfferCreateRequest,
        String commonId
    ) {
        checkAssetIdAvailable(dsl, commonId);
        return assetApiService.createAsset(dsl, dataOfferCreateRequest.getAsset());
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
