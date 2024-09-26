package de.sovity.edc.utils.config;

import de.sovity.edc.utils.config.model.ConfigProp;
import org.eclipse.edc.boot.system.DefaultServiceExtensionContext;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ConfigurationExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Custom {@link ServiceExtensionContext} for applying config defaults on EDC startup
 */
public class SovityServiceExtensionContext extends DefaultServiceExtensionContext {
    /**
     * Will be evaluated in sequence to apply default config values and validate the configuration.
     */
    private final List<ConfigProp> configProps;
    private final List<ConfigurationExtension> configurationExtensions;
    private final Config config;

    public SovityServiceExtensionContext(Monitor monitor, List<ConfigProp> configProps, List<ConfigurationExtension> configurationExtensions) {
        super(monitor, configurationExtensions);
        this.configProps = configProps;
        this.configurationExtensions = configurationExtensions;
        this.config = loadValidatedConfigWithDefaults();
    }

    @Override
    public Config getConfig(String path) {
        return this.config.getConfig(path);
    }

    private Config loadValidatedConfigWithDefaults() {
        var configs = new ArrayList<Config>();

        this.configurationExtensions.stream()
            .map(ConfigurationExtension::getConfig)
            .filter(Objects::nonNull)
            .forEach(configs::add);

        configs.add(ConfigFactory.fromEnvironment(System.getenv()));
        configs.add(ConfigFactory.fromProperties(System.getProperties()));

        var rawConfig = configs.stream().reduce(Config::merge)
            .orElseGet(ConfigFactory::empty);

        var configService = new ConfigService(getMonitor(), configProps);
        return ConfigFactory.fromMap(configService.applyDefaults(rawConfig.getEntries()));
    }
}
