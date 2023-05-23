package de.sovity.edc.ext.wrapper.api.offering.services;

import de.sovity.edc.ext.wrapper.api.offering.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.offering.model.PolicyDefinitionRequestDto;
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
     * @param createOfferingDto DTO containing the necessary data.
     */
    public void create(CreateOfferingDto createOfferingDto) {
        var asset = transformAsset(createOfferingDto.getAssetEntryDto());
        var dataAddress = transformDataAddress(createOfferingDto.getAssetEntryDto());
        var policy = transformPolicy(createOfferingDto.getPolicyDefinitionRequestDto());
        var contractDefinition = transformContractDefinition(createOfferingDto
                .getContractDefinitionRequestDto());

        persist(asset, dataAddress, policy, contractDefinition);
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
        var policy = policyMappingService.policyDtoToPolicy(dto.getPolicy());
        return PolicyDefinition.Builder.newInstance()
                .id(dto.getId())
                .policy(policy)
                .build();
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
