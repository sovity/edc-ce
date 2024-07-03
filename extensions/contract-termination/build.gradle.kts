
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
    implementation(project(":extensions:sovity-messenger"))

    implementation(libs.edc.contractSpi)
    implementation(libs.edc.coreSpi)
    implementation(libs.edc.managementApiConfiguration)
    implementation(libs.edc.transferSpi)
    // TODO: why iss this only a testImpl in extensions/wrapper/wrapper/build.gradle.kts:49
    implementation(libs.edc.dspApiConfiguration)
    implementation(libs.edc.dspNegotiationTransform)

    implementation(libs.jakarta.rsApi)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(project(":extensions:wrapper:wrapper"))
    testImplementation(project(":utils:test-connector-remote"))
    testImplementation(project(":utils:test-utils"))
    testImplementation(project(":utils:versions"))

    testImplementation(libs.edc.controlPlaneCore)
    testImplementation(libs.edc.dataPlaneSelectorCore)
    testImplementation(libs.edc.dspHttpCore)
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

    testImplementation(libs.assertj.core)
    testImplementation(libs.flyway.core)
    testImplementation(libs.junit.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.testcontainers.testcontainers)
    testImplementation(libs.testcontainers.postgresql)

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
