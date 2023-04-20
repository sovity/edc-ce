package de.sovity.edc.ext.wrapper.api.offering.services;

import de.sovity.edc.ext.wrapper.api.offering.model.ContractDefinitionDto;
import de.sovity.edc.ext.wrapper.api.offering.model.CreateOfferingDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.api.transformer.DtoTransformerRegistry;
import org.eclipse.edc.connector.api.management.asset.model.AssetEntryDto;
import org.eclipse.edc.connector.api.management.contractdefinition.model.ContractDefinitionRequestDto;
import org.eclipse.edc.connector.api.management.policy.model.PolicyDefinitionRequestDto;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;

@RequiredArgsConstructor
public class OfferingService {

    private final AssetIndex assetIndex;
    private final PolicyDefinitionStore policyDefinitionStore;
    private final ContractDefinitionStore contractDefinitionStore;
    private final DtoTransformerRegistry dtoTransformerRegistry;

    public void create(CreateOfferingDto createOfferingDto) {
        createAsset(createOfferingDto.getAssetEntryDto());
        createPolicyDefinition(createOfferingDto.getPolicyDefinitionRequestDto());
        createContractDefinition(createOfferingDto.getContractDefinitionRequestDto());
    }

    private void createAsset(AssetEntryDto dto) {
        var assetResult = dtoTransformerRegistry.transform(dto.getAsset(), Asset.class);
        var dataAddressResult = dtoTransformerRegistry.transform(dto.getDataAddress(), DataAddress.class);

        if (assetResult.failed() || dataAddressResult.failed()) {
            // TODO
        }

        assetIndex.accept(assetResult.getContent(), dataAddressResult.getContent());
    }

    private void createPolicyDefinition(PolicyDefinitionRequestDto dto) {
        var result = dtoTransformerRegistry.transform(dto, PolicyDefinition.class);

        if (result.failed()) {
            // TODO
        }

        policyDefinitionStore.save(result.getContent());
    }

    private void createContractDefinition(ContractDefinitionRequestDto dto) {
        var result = dtoTransformerRegistry.transform(dto, ContractDefinition.class);

        if (result.failed()) {
            // TODO
        }

        contractDefinitionStore.save(result.getContent());
    }
}
