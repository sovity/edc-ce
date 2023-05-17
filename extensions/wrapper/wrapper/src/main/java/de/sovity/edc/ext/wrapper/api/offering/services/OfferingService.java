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

@RequiredArgsConstructor
public class OfferingService {

    private final AssetIndex assetIndex;
    private final PolicyDefinitionStore policyDefinitionStore;
    private final ContractDefinitionStore contractDefinitionStore;
    private final DtoTransformerRegistry dtoTransformerRegistry;
    private final PolicyMappingService policyMappingService;

    public void create(CreateOfferingDto createOfferingDto) {
        createAsset(createOfferingDto.getAssetEntryDto());
        createPolicyDefinition(createOfferingDto.getPolicyDefinitionRequestDto());
        createContractDefinition(createOfferingDto.getContractDefinitionRequestDto());
    }

    private void createAsset(AssetEntryDto dto) {
        var asset = dtoTransformerRegistry.transform(dto.getAsset(), Asset.class)
                .orElseThrow(failure -> new InvalidRequestException(failure.getFailureDetail()));
        var dataAddress = dtoTransformerRegistry.transform(dto.getDataAddress(),
                        DataAddress.class)
                .orElseThrow(failure -> new InvalidRequestException(failure.getFailureDetail()));
        assetIndex.accept(asset, dataAddress);
    }

    private void createPolicyDefinition(PolicyDefinitionRequestDto dto) {
        PolicyDefinition policyDefinition = PolicyDefinition.Builder.newInstance()
                .id(dto.getId())
                .policy(policyMappingService.policyDtoToPolicy(dto.getPolicy()))
                .build();

        policyDefinitionStore.save(policyDefinition);
    }

    private void createContractDefinition(ContractDefinitionRequestDto dto) {
        var result = dtoTransformerRegistry.transform(dto, ContractDefinition.class)
                .orElseThrow(failure -> new InvalidRequestException(failure.getFailureDetail()));

        contractDefinitionStore.save(result);
    }
}
