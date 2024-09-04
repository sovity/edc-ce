
plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    implementation(project(":extensions:contract-termination"))

    implementation(libs.edc.coreSpi)
    implementation(libs.edc.dspNegotiationTransform)
    implementation(libs.edc.transferSpi)

    implementation(libs.loggingHouse.client)
    implementation(libs.jakarta.rsApi)


    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":extensions:postgres-flyway"))
    testImplementation(project(":utils:test-utils"))
    testImplementation(project(":utils:versions"))

    testImplementation(libs.edc.monitorJdkLogger)
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
    testImplementation(libs.hibernate.validation)
    testImplementation(libs.jakarta.el)
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
