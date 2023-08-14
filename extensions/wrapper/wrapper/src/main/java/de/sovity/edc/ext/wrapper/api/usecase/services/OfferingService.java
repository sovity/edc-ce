package de.sovity.edc.ext.wrapper.api.usecase.services;

import de.sovity.edc.ext.wrapper.api.usecase.model.AssetEntryDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractDefinitionRequestDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.PolicyDefinitionRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;

/**
 * Service for all the features of the wrapper regarding offers.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@RequiredArgsConstructor
@Slf4j
public class OfferingService {

    private final AssetIndex assetIndex;
    private final PolicyDefinitionStore policyDefinitionStore;
    private final ContractDefinitionStore contractDefinitionStore;
    private final PolicyMappingService policyMappingService;

    /**
     * Creates the asset, policy and contract definition in the connector. First, transforms the
     * inputs to the EDC model and then persists them. If persisting one fails, none are persisted.
     *
     * @param dto DTO containing the necessary data.
     */
    public void create(CreateOfferingDto dto) {
        validateInput(dto);

        try {
            var asset = transformAsset(dto.getAssetEntry());
            var policy = transformPolicy(dto.getPolicyDefinitionRequest());
            var contractDefinition = transformContractDefinition(dto
                    .getContractDefinitionRequest());
            persist(asset, policy, contractDefinition);
        } catch (EdcPersistenceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error transforming DTOs: " + e.getMessage(), e);
            throw new InvalidRequestException(e.getMessage());
        }
    }

    private void validateInput(CreateOfferingDto dto) {
        if (dto == null) {
            throw new InvalidRequestException("No CreateOfferingDto provided");
        } else if (dto.getAssetEntry() == null) {
            throw new InvalidRequestException("No AssetEntry provided");
        } else if (dto.getPolicyDefinitionRequest() == null) {
            throw new InvalidRequestException("No PolicyDefinitionRequest provided");
        } else if (dto.getContractDefinitionRequest() == null) {
            throw new InvalidRequestException("No ContractDefinitionRequest provided");
        }
    }

    private Asset transformAsset(AssetEntryDto dto) {
        return Asset.Builder.newInstance()
                .id(dto.getId())
                .dataAddress(DataAddress.Builder.newInstance().properties(dto.getDataAddressProperties()).build())
                .properties(dto.getAssetProperties())
                .build();
    }

    private DataAddress transformDataAddress(AssetEntryDto dto) {
        return DataAddress.Builder.newInstance()
                .properties(dto.getDataAddressProperties())
                .build();
    }

    private PolicyDefinition transformPolicy(PolicyDefinitionRequestDto dto) {
        try {
            var policy = policyMappingService.policyDtoToPolicy(dto.getPolicy());
            return PolicyDefinition.Builder.newInstance()
                    .id(dto.getId())
                    .policy(policy)
                    .build();
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(e.getMessage());
        }

    }

    private ContractDefinition transformContractDefinition(ContractDefinitionRequestDto dto) {
        return ContractDefinition.Builder.newInstance()
                .id(dto.getId())
                .contractPolicyId(dto.getContractPolicyId())
                .accessPolicyId(dto.getAccessPolicyId())
                .assetsSelector(dto.getAssetsSelector().stream()
                        .map(criterionDto -> new Criterion(
                                criterionDto.getOperandLeft(),
                                criterionDto.getOperator(),
                                criterionDto.getOperandRight())).toList()
                )
                .build();
    }

    private void persist(Asset asset, PolicyDefinition policyDefinition,
                         ContractDefinition contractDefinition) {
        try {
            assetIndex.create(asset);
            policyDefinitionStore.create(policyDefinition);
            contractDefinitionStore.save(contractDefinition);
        } catch (Exception e) {
            // Persist all or none (deleteById methods do not fail if ID not found)
            assetIndex.deleteById(asset.getId());
            policyDefinitionStore.delete(policyDefinition.getId());
            contractDefinitionStore.deleteById(contractDefinition.getId());
            throw e;
        }
    }
}
