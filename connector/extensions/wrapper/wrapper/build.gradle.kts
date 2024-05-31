
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
    api(project(":extensions:wrapper:wrapper-api"))
    api(project(":extensions:wrapper:wrapper-common-mappers"))
    api(project(":utils:catalog-parser"))
    api(project(":utils:json-and-jsonld-utils"))
    api(libs.edc.contractDefinitionApi)
    api(libs.edc.controlPlaneSpi)
    api(libs.edc.coreSpi)
    api(libs.edc.policyDefinitionApi)
    api(libs.edc.transferProcessApi)
    implementation(libs.apache.commonsLang)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)

    testImplementation(project(":extensions:wrapper:clients:java-client"))
    testImplementation(project(":extensions:policy-always-true"))
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

    testImplementation(libs.edc.jsonLd)
    testImplementation(libs.edc.dspHttpSpi)
    testImplementation(libs.edc.dspApiConfiguration)
    testImplementation(libs.edc.dataPlaneSelectorCore)

    testImplementation(libs.jsonUnit.assertj)
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.mockito.core)
    testImplementation(libs.assertj.core)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
}

tasks.withType<Test> {
    maxParallelForks = 1
}

group = libs.versions.sovityEdcExtensionGroup.get()

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}
