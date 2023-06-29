package de.sovity.edc.ext.wrapper.api.usecase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ConsumeInputDto {
    String connectorId;
    String connectorAddress;
    String offerId; //TODO first part of offer id (contract-def id) needed
    Asset asset;
    Policy policy;
    DataAddress dataDestination;
    ZonedDateTime contractStart;
    ZonedDateTime contractEnd; //TODO can dates be obtained other than from user?
}
