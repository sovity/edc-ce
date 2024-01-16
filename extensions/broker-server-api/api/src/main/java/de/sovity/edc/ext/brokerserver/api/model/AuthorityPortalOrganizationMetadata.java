package de.sovity.edc.ext.brokerserver.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Information about a single organization from the Authority Portal.")
public class AuthorityPortalOrganizationMetadata {
    @Schema(description = "MDS-ID from the Authority Portal")
    private String mdsId;
    @Schema(description = "Company name")
    private String name;
}
