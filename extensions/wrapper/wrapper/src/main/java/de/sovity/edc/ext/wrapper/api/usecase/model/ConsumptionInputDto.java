package de.sovity.edc.ext.wrapper.api.usecase.model;

import de.sovity.edc.ext.wrapper.api.common.model.PolicyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Input for a consumption process.
 *
 * @author Ronja Quensel
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ConsumptionInputDto {
    /** ID of the provider. */
    private String connectorId;

    /** Address of the provider. */
    private String connectorAddress;

    /** ID of the offer which is the basis for the consumption request. */
    private String offerId;

    /** ID of the asset that is requested. */
    private String assetId;

    /** Policy used as the basis for the contract negotiation. */
    private PolicyDto policy;

    /**
     * Destination where the data should be transferred. For reference, see
     * {@link org.eclipse.edc.spi.types.domain.DataAddress}
     */
    private Map<String, String> dataDestination;
}
