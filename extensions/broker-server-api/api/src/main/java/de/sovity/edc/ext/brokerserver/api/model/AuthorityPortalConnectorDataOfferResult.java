package de.sovity.edc.ext.brokerserver.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Provides information about connectors with some meta information and data offers.", requiredMode = Schema.RequiredMode.REQUIRED)
public class AuthorityPortalConnectorDataOfferResult {
    @Schema(description = "List of connectors containing information about data offers", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AuthorityPortalConnectorDataOfferInfo> authorityPortalConnectorDataOfferInfos;
}
