plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    annotationProcessor(libs.lombok)

    compileOnly(libs.lombok)

    implementation(libs.edc.controlPlaneCore)
    implementation(libs.edc.httpSpi)

    implementation(libs.edc.dspHttpSpi)


    testAnnotationProcessor(libs.lombok)

    testCompileOnly(libs.lombok)

    testImplementation(project(":utils:test-connector-remote"))
    testImplementation(project(":extensions:sovity-custom-messages-receiver"))

    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.dataPlaneSelectorCore)
    testImplementation(libs.edc.dspApiConfiguration)
    testImplementation(libs.edc.dspHttpCore)
    testImplementation(libs.edc.iamMock)
    testImplementation(libs.edc.jsonLd)

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
    testImplementation(libs.junit.api)
    testImplementation(libs.jsonAssert)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockserver.netty)
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.testcontainers.testcontainers)
    testImplementation(libs.testcontainers.junitJupiter)
    testImplementation(libs.testcontainers.postgresql)

    testRuntimeOnly(libs.junit.engine)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
