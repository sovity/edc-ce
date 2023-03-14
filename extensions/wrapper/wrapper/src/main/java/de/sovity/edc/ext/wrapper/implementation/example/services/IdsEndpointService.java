package de.sovity.edc.ext.wrapper.implementation.example.services;

import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

@RequiredArgsConstructor
public class IdsEndpointService {
    private final Config config;

    public String getIdsEndpoint() {
        return config.getString("edc.ids.endpoint");
    }
}
