package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.DataAddress;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ConsumptionInputDto {
    String connectorId;
    String connectorAddress;
    String offerId;
    String assetId;
    Policy policy; //TODO is still using EDC model: policy as input for negotiation requires target attribute (= asset id)
    DataAddress dataDestination;
}
