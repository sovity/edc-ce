
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":utils:jooq-database-access"))
    implementation(project(":extensions:database-direct-access"))
    implementation(project(":extensions:postgres-flyway"))

    implementation(libs.edc.contractSpi)
    implementation(libs.edc.coreSpi)
    implementation(libs.edc.managementApiConfiguration)
    implementation(libs.edc.transferSpi)

    implementation(libs.jakarta.rsApi)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":utils:test-connector-remote"))
    testImplementation(libs.edc.controlPlaneCore)
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

    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.edc.dataPlaneSelectorCore)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit.api)
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
