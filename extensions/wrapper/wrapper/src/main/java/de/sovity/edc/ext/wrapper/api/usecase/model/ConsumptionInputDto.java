package de.sovity.edc.ext.wrapper.api.usecase.model;

import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.edc.spi.types.domain.DataAddress;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ConsumptionInputDto {
    private String connectorId;
    private String connectorAddress;
    private String offerId;
    private String assetId;
    private PolicyDto policy;
    private DataAddress dataDestination;
}
