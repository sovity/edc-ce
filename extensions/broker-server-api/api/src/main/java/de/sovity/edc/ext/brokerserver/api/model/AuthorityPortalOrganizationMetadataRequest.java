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
@Schema(description = "Information about organizations from the Authority Portal.")
public class AuthorityPortalOrganizationMetadataRequest {
    @Schema(description = "Organization metadata")
    private List<AuthorityPortalOrganizationMetadata> organizations;
}
