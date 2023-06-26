package de.sovity.edc.extension.e2e.connector.factory;

import de.sovity.edc.extension.e2e.connector.Connector;
import de.sovity.edc.extension.e2e.connector.TestConnector;
import de.sovity.edc.extension.e2e.connector.config.ApiConfig;
import de.sovity.edc.extension.e2e.connector.config.DatasourceConfig;
import de.sovity.edc.extension.e2e.connector.config.EdcApiType;
import de.sovity.edc.extension.e2e.connector.config.SimpleConfig;
import de.sovity.edc.extension.e2e.db.TestDatabase;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.junit.extensions.EdcExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.sovity.edc.extension.e2e.connector.config.EdcApiType.CONTROL;
import static de.sovity.edc.extension.e2e.connector.config.EdcApiType.DEFAULT;
import static de.sovity.edc.extension.e2e.connector.config.EdcApiType.MANAGEMENT;
import static de.sovity.edc.extension.e2e.connector.config.EdcApiType.PROTOCOL;
import static org.eclipse.edc.junit.testfixtures.TestUtils.getFreePort;

@RequiredArgsConstructor
public class TestConnectorFactory implements ConnectorFactory {

    private static final String BASE_URL = "http://localhost";
    private static final List<String> DATASOURCE_NAMES = List.of(
            "asset",
            "contractdefinition",
            "contractnegotiation",
            "policy",
            "transferprocess",
            "dataplaneinstance",
            "default"
    );

    private final String participantId;
    private final EdcExtension edcContext;
    private final TestDatabase testDatabase;

    @Override
    public Connector createConnector() {
        var apiConfigMap = createApiConfigMap();
        var dspCallbackAddress = apiConfigMap.get(PROTOCOL).getUri();
        var datasourceConfigs = getDatasourceConfigs(testDatabase);
        var connector = TestConnector.builder()
                .participantId(participantId)
                .apiConfigMap(apiConfigMap)
                .datasourceConfigs(datasourceConfigs)
                .simpleConfig(new SimpleConfig("edc.participant.id", participantId))
                .simpleConfig(new SimpleConfig("edc.api.auth.key", UUID.randomUUID().toString()))
                .simpleConfig(new SimpleConfig("edc.last.commit.info", "test env commit message"))
                .simpleConfig(new SimpleConfig("edc.build.date", "2023-05-08T15:30:00Z"))
                .simpleConfig(new SimpleConfig("edc.jsonld.https.enabled", "true"))
                .simpleConfig(new SimpleConfig("edc.dsp.callback.address",
                        dspCallbackAddress.toString()))
                .simpleConfig(new SimpleConfig("edc.flyway.additional.migration.locations",
                        "classpath:migration/" + participantId + ",classpath:migration/version010"))
                .build();
        System.out.println(connector.getConfig());
        edcContext.setConfiguration(connector.getConfig());
        return connector;
    }

    private Map<EdcApiType, ApiConfig> createApiConfigMap() {
        return Map.of(
                DEFAULT, new ApiConfig(BASE_URL, "", "/api", getFreePort()),
                PROTOCOL, new ApiConfig(BASE_URL, "protocol", "/protocol", getFreePort()),
                MANAGEMENT, new ApiConfig(BASE_URL, "management", "/api/management", getFreePort()),
                CONTROL, new ApiConfig(BASE_URL, "control", "/control", getFreePort())
        );
    }

    private List<DatasourceConfig> getDatasourceConfigs(TestDatabase testDatabase) {
        return DATASOURCE_NAMES.stream()
                .map(name -> new DatasourceConfig(
                        name,
                        testDatabase.getJdbcUrl(),
                        testDatabase.getJdbcUser(),
                        testDatabase.getJdbcPassword()))
                .toList();
    }
}
