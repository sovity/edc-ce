package de.sovity.edc.extension.postgresql;

import org.eclipse.edc.connector.dataplane.selector.store.sql.schema.DataPlaneInstanceStatements;
import org.eclipse.edc.connector.dataplane.selector.store.sql.schema.postgres.PostgresDataPlaneInstanceStatements;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.system.ServiceExtension;

public class DataPlaneInstanceStatementsProviderExtension implements ServiceExtension {


    @Override
    public String name() {
        return "DataPlaneInstance Statements Provider";
    }

    @Provider
    public DataPlaneInstanceStatements dataPlaneInstanceStatements() {
        return new PostgresDataPlaneInstanceStatements();
    }
}