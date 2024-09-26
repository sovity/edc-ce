package de.sovity.edc;

import de.sovity.edc.utils.config.ConfigProps;
import de.sovity.edc.utils.config.SovityEdcRuntime;

public class Main {
    public static void main(String[] args) {
        SovityEdcRuntime.boot(ConfigProps.ALL_CE_PROPS);
    }
}
