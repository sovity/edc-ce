
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(libs.edc.apiCore)
    implementation(libs.edc.managementApiConfiguration)
    implementation(libs.edc.dspHttpSpi)
    api(project(":extensions:contract-termination"))
    api(project(":extensions:database-direct-access"))
    api(project(":extensions:sovity-messenger"))
    api(project(":extensions:wrapper:wrapper-api"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))
    api(project(":utils:catalog-parser"))
    api(project(":utils:jooq-database-access"))
    api(project(":utils:json-and-jsonld-utils"))
    api(project(":utils:test-connector-remote"))
    api(libs.edc.contractDefinitionApi)
    api(libs.edc.controlPlaneSpi)
    api(libs.edc.coreSpi)
    api(libs.edc.policyDefinitionApi)
    api(libs.edc.transferProcessApi)
    implementation(libs.apache.commonsLang)
    implementation(libs.jooq.jooq)
    implementation(libs.hikari)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":extensions:database-direct-access"))
    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(project(":extensions:policy-always-true"))
    testImplementation(project(":utils:jooq-database-access"))
    testImplementation(project(":utils:test-utils"))
    testImplementation(libs.edc.controlPlaneCore)
    testImplementation(libs.edc.dsp)
    testImplementation(libs.edc.iamMock)
    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.http) {
        exclude(group = "org.eclipse.jetty", module = "jetty-client")
        exclude(group = "org.eclipse.jetty", module = "jetty-http")
        exclude(group = "org.eclipse.jetty", module = "jetty-io")
        exclude(group = "org.eclipse.jetty", module = "jetty-server")
        exclude(group = "org.eclipse.jetty", module = "jetty-util")
        exclude(group = "org.eclipse.jetty", module = "jetty-webapp")
    }

    // Updated jetty versions for e.g. CVE-2023-26048
    testImplementation(libs.bundles.jetty.cve2023)

    testImplementation(libs.edc.controlPlaneSql)
    testImplementation(libs.edc.contractNegotiationStoreSql)
    testImplementation(libs.edc.dspHttpSpi)
    testImplementation(libs.edc.dspApiConfiguration)
    testImplementation(libs.edc.dataPlaneSelectorCore)
    testImplementation(libs.edc.jsonLd)
    testImplementation(libs.edc.transferProcessStoreSql) // TODO: try to reduce scope. This is needed for the transfer's store

    testImplementation(libs.jsonUnit.assertj)
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)


    /// TODO rm deps after here


    // Control-Plane
    api(libs.edc.controlPlaneCore)
    api(libs.edc.managementApi)
    api(libs.edc.apiObservability)
    api(libs.edc.configurationFilesystem)
    api(libs.edc.controlPlaneAggregateServices)
    api(libs.edc.http)
    api(libs.edc.dsp)
    api(libs.edc.jsonLd)

    // Data Management API Key
    api(libs.edc.authTokenbased)

    // sovity Extensions Package
    // Policies
    api(project(":extensions:policy-referring-connector"))
    api(project(":extensions:policy-time-interval"))
    api(project(":extensions:policy-always-true"))

    // API Extensions
    api(project(":extensions:edc-ui-config"))
    api(project(":extensions:last-commit-info"))

    api(project(":extensions:postgres-flyway"))
    api(project(":extensions:transfer-process-status-checker"))

    // Control-plane to Data-plane
    api(libs.edc.transferDataPlane)
    api(libs.edc.dataPlaneSelectorCore)
    api(libs.edc.dataPlaneSelectorClient)

    // Data-plane
    api(libs.edc.dataPlaneHttp)
    api(libs.edc.dataPlaneFramework)
    api(libs.edc.dataPlaneCore)
    api(libs.edc.dataPlaneUtil)

    api(project(":launchers:common:auth-mock"))

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(project(":utils:test-utils"))
    testImplementation(project(":extensions:test-backend-controller"))
    testImplementation(project(":utils:test-connector-remote"))
    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(libs.jsonUnit.assertj)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testImplementation(libs.mockserver.netty)
    testImplementation(libs.restAssured.restAssured)
    testRuntimeOnly(libs.junit.engine)
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
