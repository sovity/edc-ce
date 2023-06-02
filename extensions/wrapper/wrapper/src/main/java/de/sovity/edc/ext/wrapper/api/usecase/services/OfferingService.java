package de.sovity.edc.ext.wrapper.api.usecase.services;

import de.sovity.edc.ext.wrapper.api.usecase.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.PolicyDefinitionRequestDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.api.transformer.DtoTransformerRegistry;
import org.eclipse.edc.connector.api.management.asset.model.AssetEntryDto;
import org.eclipse.edc.connector.api.management.contractdefinition.model.ContractDefinitionRequestDto;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;

/**
 * Service for all the features of the wrapper regarding offers.
 *
 * @author tim.dahlmanns@isst.fraunhofer.de
 */
@RequiredArgsConstructor
public class OfferingService {

    private final AssetIndex assetIndex;
    private final PolicyDefinitionStore policyDefinitionStore;
    private final ContractDefinitionStore contractDefinitionStore;
    private final DtoTransformerRegistry dtoTransformerRegistry;
    private final PolicyMappingService policyMappingService;

    /**
     * Creates the asset, policy and contract definition in the connector. First, transforms the
     * inputs to the EDC model and then persists them. If persisting one fails, none are persisted.
     *
     * @param dto DTO containing the necessary data.
     */
    public void create(CreateOfferingDto dto) {
        validateInput(dto);

        var asset = transformAsset(dto.getAssetEntry());
        var dataAddress = transformDataAddress(dto.getAssetEntry());
        var policy = transformPolicy(dto.getPolicyDefinitionRequest());
        var contractDefinition = transformContractDefinition(dto
                .getContractDefinitionRequest());

        persist(asset, dataAddress, policy, contractDefinition);
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
        return dtoTransformerRegistry.transform(dto.getAsset(), Asset.class)
                .orElseThrow(failure -> new InvalidRequestException(failure.getFailureDetail()));
    }

    private DataAddress transformDataAddress(AssetEntryDto dto) {
        return dtoTransformerRegistry.transform(dto.getDataAddress(), DataAddress.class)
                .orElseThrow(failure -> new InvalidRequestException(failure.getFailureDetail()));
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
        return dtoTransformerRegistry.transform(dto, ContractDefinition.class)
                .orElseThrow(failure -> new InvalidRequestException(failure.getFailureDetail()));
    }

    private void persist(Asset asset, DataAddress dataAddress, PolicyDefinition policyDefinition,
                         ContractDefinition contractDefinition) {
        try {
            assetIndex.accept(asset, dataAddress);
            policyDefinitionStore.save(policyDefinition);
            contractDefinitionStore.save(contractDefinition);
        } catch (Exception e) {
            // Persist all or none (deleteById methods do not fail if ID not found)
            assetIndex.deleteById(asset.getId());
            policyDefinitionStore.deleteById(policyDefinition.getId());
            contractDefinitionStore.deleteById(contractDefinition.getId());
            throw e;
        }
    }
}
