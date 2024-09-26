package de.sovity.edc.utils.config;

import de.sovity.edc.utils.config.model.ConfigProp;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.boot.system.runtime.BaseRuntime;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Custom {@link BaseRuntime} for applying config defaults on EDC startup
 */
@RequiredArgsConstructor
public class SovityEdcRuntime extends BaseRuntime {
    /**
     * Will be evaluated in sequence to apply default config values and validate the configuration.
     */
    private final List<ConfigProp> configProps;

    public static void boot(List<ConfigProp> configProps) {
        var runtime = new SovityEdcRuntime(configProps);
        runtime.boot();
    }

    @Override
    protected @NotNull ServiceExtensionContext createContext(Monitor monitor) {
        return new SovityServiceExtensionContext(monitor, configProps, this.loadConfigurationExtensions());
    }
}
