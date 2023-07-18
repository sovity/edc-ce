package de.sovity.edc.ext.wrapper.api.usecase.services;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.sovity.edc.ext.wrapper.api.common.model.PermissionDto;
import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import de.sovity.edc.ext.wrapper.api.common.model.AssetEntryDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.ContractDefinitionRequestDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.CreateOfferingDto;
import de.sovity.edc.ext.wrapper.api.usecase.model.PolicyDefinitionRequestDto;
import java.util.ArrayList;
import java.util.Map;
import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.persistence.EdcPersistenceException;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OfferingServiceTest {

    private final AssetIndex assetIndex = mock(AssetIndex.class);
    private final PolicyDefinitionStore policyDefinitionStore = mock(PolicyDefinitionStore.class);
    private final ContractDefinitionStore contractDefinitionStore = mock(
            ContractDefinitionStore.class);
    private final PolicyMappingService policyMappingService = mock(PolicyMappingService.class);

    private OfferingService offeringService;

    private AssetEntryDto assetEntryDto;
    private Asset asset;
    private DataAddress dataAddress;
    private PolicyDefinitionRequestDto policyDefinitionDto;
    private Policy policy;
    private ContractDefinitionRequestDto contractDefinitionDto;
    private ContractDefinition contractDefinition;
    private CreateOfferingDto createOfferingDto;

    @BeforeEach
    void setUp() {
        this.offeringService = new OfferingService(assetIndex, policyDefinitionStore,
                contractDefinitionStore, policyMappingService);

        this.assetEntryDto = assetDto();
        this.asset = asset();
        this.dataAddress = dataAddress();
        this.policyDefinitionDto = policyDefinitionDto();
        this.policy = policy();
        this.contractDefinitionDto = contractDefinitionDto();
        this.contractDefinition = contractDefinition();
        this.createOfferingDto = new CreateOfferingDto(assetEntryDto, policyDefinitionDto,
                contractDefinitionDto);

        when(policyMappingService.policyDtoToPolicy(policyDefinitionDto.getPolicy())).thenReturn(
                policy);
    }

    @Test
    void create_validInput_createResource() {
        // act
        offeringService.create(createOfferingDto);

        // assert
        verify(assetIndex, times(1)).create(argThat(def -> asset.getId().equals(def.getId())),
                argThat(def -> dataAddress.getType().equals(def.getType())));
        verify(policyDefinitionStore, times(1)).create(argThat(def ->
                policy.equals(def.getPolicy()) && policyDefinitionDto.getId().equals(def.getId())));
        verify(contractDefinitionStore, times(1)).save(contractDefinition);
    }

    @Test
    void create_assetTransformationFails_throwException() {
        // arrange
        assetEntryDto.setDataAddressProperties(Map.of());

        // act && assert
        assertThatThrownBy(() -> offeringService.create(createOfferingDto))
                .isInstanceOf(InvalidRequestException.class);

        verifyNoInteractions(assetIndex);
        verifyNoInteractions(policyDefinitionStore);
        verifyNoInteractions(contractDefinitionStore);
    }

    @Test
    void create_persistingAssetFails_throwException() {
        // arrange
        doThrow(NullPointerException.class).when(assetIndex).create(any(), any());

        // act && assert
        assertThatThrownBy(() -> offeringService.create(createOfferingDto))
                .isInstanceOf(NullPointerException.class);

        verify(assetIndex, times(1)).create(argThat(def -> asset.getId().equals(def.getId())),
                argThat(def -> dataAddress.getType().equals(def.getType())));
        verify(assetIndex, times(1)).deleteById(asset.getId());
        verify(policyDefinitionStore, times(1))
                .delete(policyDefinitionDto.getId());
        verify(contractDefinitionStore, times(1))
                .deleteById(contractDefinition.getId());
    }

    @Test
    void create_mappingPolicyFails_throwException() {
        // arrange
        doThrow(IllegalArgumentException.class).when(policyMappingService)
                .policyDtoToPolicy(policyDefinitionDto.getPolicy());

        // act && assert
        assertThatThrownBy(() -> offeringService.create(createOfferingDto))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(assetIndex);
        verifyNoInteractions(policyDefinitionStore);
        verifyNoInteractions(contractDefinitionStore);
    }

    @Test
    void create_persistingPolicyFails_throwException() {
        // arrange
        doThrow(EdcPersistenceException.class).when(policyDefinitionStore).create(any());

        // act && assert
        assertThatThrownBy(() -> offeringService.create(createOfferingDto))
                .isInstanceOf(EdcPersistenceException.class);

        verify(assetIndex, times(1)).create(argThat(def -> asset.getId().equals(def.getId())),
                argThat(def -> dataAddress.getType().equals(def.getType())));
        verify(assetIndex, times(1)).deleteById(asset.getId());
        verify(policyDefinitionStore, times(1)).create(argThat(pd ->
                policy.equals(pd.getPolicy()) && policyDefinitionDto.getId().equals(pd.getId())));
        verify(policyDefinitionStore, times(1))
                .delete(policyDefinitionDto.getId());
        verify(contractDefinitionStore, times(1))
                .deleteById(contractDefinition.getId());
    }

    @Test
    void create_contractDefinitionTransformationFails_throwException() {
        // arrange
        contractDefinitionDto.setAssetsSelector(null);

        // act && assert
        assertThatThrownBy(() -> offeringService.create(createOfferingDto))
                .isInstanceOf(InvalidRequestException.class);

        verifyNoInteractions(assetIndex);
        verifyNoInteractions(policyDefinitionStore);
        verifyNoInteractions(contractDefinitionStore);
    }

    @Test
    void create_persistingContractDefinitionFails_throwException() {
        // arrange
        doThrow(EdcPersistenceException.class).when(contractDefinitionStore)
                .save(any(ContractDefinition.class));

        // act && assert
        assertThatThrownBy(() -> offeringService.create(createOfferingDto))
                .isInstanceOf(EdcPersistenceException.class);

        verify(assetIndex, times(1)).create(argThat(def -> asset.getId().equals(def.getId())),
                argThat(def -> dataAddress.getType().equals(def.getType())));
        verify(assetIndex, times(1)).deleteById(asset.getId());
        verify(policyDefinitionStore, times(1)).create(argThat(pd ->
                policy.equals(pd.getPolicy()) && policyDefinitionDto.getId().equals(pd.getId())));
        verify(policyDefinitionStore, times(1))
                .delete(policyDefinitionDto.getId());
        verify(contractDefinitionStore, times(1)).save(contractDefinition);
        verify(contractDefinitionStore, times(1))
                .deleteById(contractDefinition.getId());
    }

    private AssetEntryDto assetDto() {
        return AssetEntryDto.builder()
                .id("asset-id")
                .assetProperties(Map.of())
                .dataAddressProperties(Map.of("type", "type"))
                .build();
    }

    private Asset asset() {
        return Asset.Builder.newInstance().id("asset-id").build();
    }

    private DataAddress dataAddress() {
        return DataAddress.Builder.newInstance().type("type").build();
    }

    private PolicyDefinitionRequestDto policyDefinitionDto() {
        var permission = PermissionDto.builder().build();
        var policy = PolicyDto.builder().permission(permission).build();
        return new PolicyDefinitionRequestDto("policy-id", policy);
    }

    private Policy policy() {
        return Policy.Builder.newInstance()
                .permission(Permission.Builder.newInstance()
                        .action(Action.Builder.newInstance().type("USE").build())
                        .build())
                .build();
    }

    private ContractDefinitionRequestDto contractDefinitionDto() {
        return ContractDefinitionRequestDto.builder()
                .id("contract-definition-id")
                .accessPolicyId("policy-id")
                .contractPolicyId("policy-id")
                .assetsSelector(new ArrayList<>())
                .build();
    }

    private ContractDefinition contractDefinition() {
        return ContractDefinition.Builder.newInstance()
                .id("contract-definition-id")
                .accessPolicyId("policy-id")
                .contractPolicyId("policy-id")
                .build();
    }
}
